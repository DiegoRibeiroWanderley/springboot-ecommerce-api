package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.mapper.AddressMapper;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = addressMapper.toAddress(addressDTO);
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        return addressMapper.toDTO(savedAddress);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addressMapper.toDTOs(addresses);
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return addressMapper.toDTO(address);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> userAddresses = addressRepository.findByUser(user);
        return addressMapper.toDTOs(userAddresses);
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO, User user) {
        addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        Address address = addressMapper.toAddress(addressDTO);
        address.setAddressId(addressId);
        address.setUser(user);

        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toDTO(updatedAddress);
    }

    @Transactional
    @Override
    public String deleteAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        User user = address.getUser();

        user.getAddresses().remove(address);
        addressRepository.deleteById(addressId);

        return "Address with id: " + addressId + " has been deleted";
    }
}
