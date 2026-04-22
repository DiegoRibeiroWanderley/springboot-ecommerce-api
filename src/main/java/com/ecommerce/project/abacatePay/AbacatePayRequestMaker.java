package com.ecommerce.project.abacatePay;

import com.ecommerce.project.abacatePay.request.AbacatePayRequest;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.mapper.AbacatePayItemMapper;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.CartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbacatePayRequestMaker {

    private final CartRepository cartRepository;
    private final AbacatePayItemMapper abacatePayItemMapper;

    @Transactional
    public AbacatePayRequest makeAbacatePayRequest(Long cartId, String method, AbacatePayUrl abacatePayUrl) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", cartId));

        User user = cart.getUser();

        List<AbacatePayItem> abacatePayItems = cart.getCartItems().stream().map(item -> {
            Product p = item.getProduct();
            AbacatePayItem abacatePayItem =  abacatePayItemMapper.toAbacatePayItem(p);
            abacatePayItem.setPrice(p.getPrice() * 100);
            abacatePayItem.setQuantity(item.getQuantity());
            return  abacatePayItem;
        }).toList();

        return AbacatePayRequest.builder()
                .frequency("ONE_TIME")
                .products(abacatePayItems)
                .methods(new ArrayList<>(List.of(method)))
                .returnUrl(abacatePayUrl.getReturnUrl())
                .completionUrl(abacatePayUrl.getCompletionUrl())
                .customerId(user.getAbacatePayId())
                .build();
    }
}
