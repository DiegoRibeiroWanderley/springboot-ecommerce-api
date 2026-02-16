package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.mapper.CartMapper;
import com.ecommerce.project.mapper.ProductMapper;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final FileService fileService;
    private final CartService cartService;

    private final ProductMapper productMapper;
    private final CartMapper cartMapper;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Override
    public ProductResponse getAllProducts(String keyword, String category, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Product> spec = getProductSpecification(keyword, category);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<Product> products = productPage.getContent();

        if (products.isEmpty()) {
            throw new APIException("No products found");
        }

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO productDTO = productMapper.toDTO(product);
                    productDTO.setImage(constructImageUrl(product.getImage()));
                    return productDTO;
                }).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    private static Specification<Product> getProductSpecification(String keyword, String category) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("productName")), "%" + keyword.toLowerCase() + "%"));
        }

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like((root.get("category").get("categoryName")), category));
        }
        return spec;
    }

    private String constructImageUrl(String imageName) {
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageable);

        List<Product> products = productPage.getContent();

        if (products.isEmpty()) {
            throw new APIException("No products with category: " + category.getCategoryName());
        }

        List<ProductDTO> productsDTO = productMapper.toDTOs(products);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productsDTO);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageable);

        List<Product> products = productPage.getContent();

        if (products.isEmpty()) {
            throw new APIException("No products with keyword: " + keyword);
        }

        List<ProductDTO> productsDTO = productMapper.toDTOs(products);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productsDTO);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());

        return productResponse;
    }

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

        Product product = productMapper.toEntity(productDTO);
        Product productFromDB = productRepository.findByProductName(product.getProductName());

        if  (productFromDB != null) {
            throw new APIException("Product with name " + product.getProductName() + " already exists");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        product.setImage("default.png");
        product.setCategory(category);
        product.setSpecialPrice(product.getPrice() - (product.getDiscount() * 0.01) * product.getPrice());

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {

        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = productMapper.toEntity(productDTO);

        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setSpecialPrice(product.getSpecialPrice());

        Product updatedProduct = productRepository.save(productFromDB);

        List<Cart> carts = cartRepository.findCartByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = cartMapper.toDTO(cart);
                    List<ProductDTO> productDTOS = cart.getCartItems().stream()
                            .map(p -> productMapper.toDTO(p.getProduct()))
                            .toList();
                    cartDTO.setProducts(productDTOS);

                    return cartDTO;
                }).toList();

        cartDTOS.forEach(cartDTO -> cartService.updateProductInCarts(cartDTO.getCartId(), productId));

        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        Product productFromDB = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));


        String fileName = fileService.uploadImage(path, image);

        productFromDB.setImage(fileName);
        Product updatedProduct = productRepository.save(productFromDB);

        return productMapper.toDTO(updatedProduct);
    }

    @Transactional
    @Override
    public ProductDTO deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        List<CartItem> itemsToRemove = cartItemRepository.findByProductId(productId);

        itemsToRemove.forEach(cartItem -> {
            Cart cart = cartItem.getCart();

            double amountToSubtract = cartItem.getProductPrice() * cartItem.getQuantity();
            cart.setTotalPrice(cart.getTotalPrice() - (amountToSubtract));

            cart.getCartItems().remove(cartItem);

            cartItem.setProduct(null);

            cartItemRepository.deleteById(cartItem.getCartItemId());
            cartRepository.save(cart);
        });

        productRepository.delete(product);
        return productMapper.toDTO(product);
    }
}