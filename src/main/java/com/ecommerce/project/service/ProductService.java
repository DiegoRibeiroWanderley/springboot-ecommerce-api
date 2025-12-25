package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.stereotype.Service;

public interface ProductService {

    ProductResponse getAllProducts();
    ProductResponse searchByCategory(Long categoryId);
    ProductResponse searchByKeyword(String keyword);
    ProductDTO addProduct(Long categoryId, Product product);
    ProductDTO updateProduct(Product product, Long productId);
}
