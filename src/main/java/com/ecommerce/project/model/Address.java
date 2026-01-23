package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @NotBlank(message = "Street name must not be blank")
    @Size(min = 5, message = "Street name must be at least 5 characters")
    private String street;

    @NotBlank(message = "Building name must not be blank")
    @Size(min = 5, message = "Building name must be at least 5 characters")
    private String buildingName;

    @NotBlank(message = "City name must not be blank")
    @Size(min = 4, message = "City name must be at least 4 characters")
    private String city;

    @NotBlank(message = "State name must not be blank")
    @Size(min = 2, message = "State name must be at least 2 characters")
    private String state;

    @NotBlank(message = "Country name must not be blank")
    @Size(min = 2, message = "Country name must be at least 2 characters")
    private String country;

    @NotBlank(message = "Postal code name must not be blank")
    @Size(min = 6, message = "PINCode name must be at least 6 characters")
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
