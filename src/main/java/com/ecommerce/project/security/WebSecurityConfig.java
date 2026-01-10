package com.ecommerce.project.security;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authJwtTokenFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsServiceImpl);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/auth/**").permitAll()
                        //.requestMatchers("/api/public/**").permitAll()
                        //.requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .anyRequest()
                        .authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
        ));
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = Role.builder().roleName(AppRole.ROLE_USER).build();
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = Role.builder().roleName(AppRole.ROLE_SELLER).build();
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = Role.builder().roleName(AppRole.ROLE_ADMIN).build();
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);

            if (!userRepository.existsByUsername("user1")) {
                User user1 = User.builder()
                        .username("user1")
                        .email("user1@example.com")
                        .password(passwordEncoder.encode("password1"))
                        .build();
                userRepository.save(user1);
            }

            if (!userRepository.existsByUsername("seller1")) {
                User seller1 = User.builder()
                        .username("seller1")
                        .email("seller1@example.com")
                        .password(passwordEncoder.encode("password2"))
                        .build();
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("adminPassword"))
                        .build();
                userRepository.save(admin);
            }

            userRepository.findByUsername("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUsername("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByUsername("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };
    }
}
