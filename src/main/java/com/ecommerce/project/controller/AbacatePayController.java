package com.ecommerce.project.controller;

import com.ecommerce.project.abacatePay.AbacatePayRequestMaker;
import com.ecommerce.project.abacatePay.AbacatePayUrl;
import com.ecommerce.project.abacatePay.request.AbacatePayRequest;
import com.ecommerce.project.abacatePay.request.CustomerRequest;
import com.ecommerce.project.abacatePay.response.AbacatePayResponse;
import com.ecommerce.project.abacatePay.response.CustomerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class AbacatePayController {

    @Value("${spring.app.abacatePayKey}")
    private String abacatePayKey;

    private final RestClient restClient;
    private final AbacatePayRequestMaker abacatePayRequestMaker;

    public AbacatePayController(RestClient.Builder builder, AbacatePayRequestMaker abacatePayRequestMaker) {
        this.restClient = builder.baseUrl("https://api.abacatepay.com/v1").build();
        this.abacatePayRequestMaker = abacatePayRequestMaker;
    }

    @PostMapping("/payment/cart/{cartId}/{method}")
    public ResponseEntity<AbacatePayResponse> createBilling(@PathVariable Long cartId, @PathVariable String method, @RequestBody AbacatePayUrl abacatePayUrl) {

        AbacatePayRequest abacatePayRequest = abacatePayRequestMaker.makeAbacatePayRequest(cartId, method, abacatePayUrl);

        System.out.println("abacatePayRequest: " + abacatePayRequest);

        AbacatePayResponse abacatePayResponse = restClient.post()
                .uri("/billing/create")
                .header("Authorization", "Bearer " + abacatePayKey)
                .body(abacatePayRequest)
                .retrieve()
                .body(AbacatePayResponse.class);

        return ResponseEntity.ok(abacatePayResponse);
    }

    public String createCustomer(CustomerRequest customer) {
        CustomerResponse abacatePayCustomerResponse = restClient.post()
                .uri("/customer/create")
                .header("Authorization", "Bearer " + abacatePayKey)
                .body(customer)
                .retrieve()
                .body(CustomerResponse.class);

        return abacatePayCustomerResponse.getData().getId();
    }
}
