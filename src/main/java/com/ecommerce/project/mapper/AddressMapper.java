package com.ecommerce.project.mapper;

import com.ecommerce.project.model.Address;
import com.ecommerce.project.payload.AddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDTO toDTO(Address address);
    List<AddressDTO> toDTOs(List<Address> addresses);

    @Mapping(target = "user", ignore = true)
    Address toAddress(AddressDTO addressDTO);
}
