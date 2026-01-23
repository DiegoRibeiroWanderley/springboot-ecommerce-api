package com.ecommerce.project.controller;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    private final AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@RequestBody @Valid AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();

        AddressDTO createdAddress = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(createdAddress,HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        List<AddressDTO> addresses = addressService.getAddresses();
        return new ResponseEntity<>(addresses ,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddress() {
        User user = authUtil.loggedInUser();

        List<AddressDTO> addressesDTO = addressService.getUserAddresses(user);
        return new ResponseEntity<>(addressesDTO,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId,
                                                        @RequestBody @Valid AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();

        AddressDTO updateAddress = addressService.updateAddressById(addressId,  addressDTO, user);
        return new ResponseEntity<>(updateAddress,HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> updateAddressById(@PathVariable Long addressId) {
        String status = addressService.deleteAddressById(addressId);
        return new ResponseEntity<>(status ,HttpStatus.OK);
    }
}
