package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.mapper.CartMapper;
import com.ecommerce.project.mapper.ProductMapper;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private final CartMapper cartMapper;
    private final AuthUtil authUtil;
    private final ProductMapper productMapper;

    @Transactional
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make and order of the "
                    + product.getProductName() + " less than or equal to " + product.getQuantity());
        }

        CartItem newCartItem = CartItem.builder()
                .product(product)
                .productPrice(product.getSpecialPrice())
                .cart(cart)
                .quantity(quantity)
                .discount(product.getDiscount())
                .build();

        cartItemRepository.save(newCartItem);
        cart.getCartItems().add(newCartItem);

        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice() * quantity);

        cartRepository.save(cart);
        CartDTO cartDTO = cartMapper.toDTO(cart);

        List<CartItem> cartItems = cart.getCartItems();
        List<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = productMapper.toDTO(item.getProduct());
            map.setQuantity(item.getQuantity());

            return map;
        }).toList();

        cartDTO.setProducts(productStream);
        return cartDTO;
    }

    @Transactional
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.isEmpty()) {
            throw new APIException("No carts found");
        }

        List<CartDTO> cartDTOS = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = cartMapper.toDTO(cart);
                    List<ProductDTO> productDTOS = cart.getCartItems().stream()
                            .map(cartItem -> {
                                ProductDTO productDTO = productMapper.toDTO(cartItem.getProduct());
                                productDTO.setQuantity(cartItem.getQuantity());
                                return productDTO;
                            }).toList();
                    cartDTO.setProducts(productDTOS);
                    return cartDTO;
                }).toList();

        return cartDTOS;
    }

    @Transactional
    @Override
    public CartDTO getCart(String email, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCart(email, cartId);

        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        CartDTO cartDTO = cartMapper.toDTO(cart);
        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(item -> {
                    ProductDTO productDTO = productMapper.toDTO(item.getProduct());
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                }).toList();
        cartDTO.setProducts(productDTOS);

        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", userCart.getCartId()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if (product.getQuantity() == 0) {
            throw new APIException("Product " + product.getProductName() + " not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make and order of the "
                    + product.getProductName() + " less than or equal to " + product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available");
        }

        int newQuantity = cartItem.getQuantity() + quantity;

        if (newQuantity == 0) {
            deleteProductFromCart(cartId, productId);
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());

            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            cartRepository.save(cart);
            cartItemRepository.save(cartItem);
        }

        CartDTO cartDTO = cartMapper.toDTO(cart);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOS = cartItems.stream()
                .map(item -> {
                    ProductDTO map = productMapper.toDTO(item.getProduct());
                    map.setQuantity(item.getQuantity());
                    return map;
                });

        cartDTO.setProducts(productDTOS.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        cart.getCartItems().remove(cartItem);

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        cartRepository.save(cart);

        return "Product " + cartItem.getProduct().getProductName() + " has been removed form the cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available");
        }

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice +  (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = Cart.builder()
                .totalPrice(0.0)
                .user(authUtil.loggedInUser())
                .cartItems(new ArrayList<>())
                .build();

        return cartRepository.save(cart);
    }
}