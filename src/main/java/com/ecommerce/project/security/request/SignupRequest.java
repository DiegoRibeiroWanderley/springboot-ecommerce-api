package com.ecommerce.project.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 30, message = "Username size must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Size(max = 50, message = "Email size must be up to 50 characters")
    @Email
    private String email;

    private Set<String> roles;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 20, message = "Password size must be between 8 and 20 characters")
    private String password;
}
