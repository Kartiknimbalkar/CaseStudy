package com.pharmacy.gateway.config;

import com.pharmacy.gateway.jwtFilter.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        System.out.println("Enter in SecurityWebFilterChain");

        return http.authorizeExchange(exchange -> exchange

                        // public  endpoint for token generation
                        .pathMatchers("/auth-service/auth/login").permitAll()
                        .pathMatchers("/auth-service/auth/register").permitAll()

                        // drug-service access control
                        // For doctor and admin user
                        .pathMatchers("/drug-service/drugs/getAll").hasAnyRole("ADMIN", "DOCTOR")
                        .pathMatchers("/drug-service/drugs/get/**").hasAnyRole("ADMIN", "DOCTOR")
                        .pathMatchers("/drug-service/drugs/update/**").hasRole("ADMIN")
                        //For admin only
                        .pathMatchers("/drug-service/drugs/**").hasRole("ADMIN")

                        // order-service access control
                        .pathMatchers("/order-service/orders/place").hasAnyRole("ADMIN", "DOCTOR")
                        .pathMatchers("/order-service/orders/verify/**").hasRole("ADMIN")
                        .pathMatchers("/order-service/orders/list").hasRole("ADMIN")
                        .pathMatchers("/order-service/orders/pickedUpOrders").hasRole("ADMIN")
                        .pathMatchers("/order-service/orders/pickedup/**").hasRole("ADMIN")

                        // sales-service access control for admin user only
                        .pathMatchers("/sales-service/sales/**").hasRole("ADMIN")

                        // supplier-service access control for admin user only
                        .pathMatchers("/supplier-service/suppliers/**").hasRole("ADMIN")
                        
                        .pathMatchers("/auth-service/auth/me")    
                        .authenticated()

                    // ADMIN-only access
                    .pathMatchers("/auth-service/auth/users/{username}") 
                        .hasRole("ADMIN")

                    // allow self-update or admin-update (method security enforces owner-or-admin)
                    .pathMatchers(HttpMethod.PUT, "/auth-service/auth/users/{username}")
                        .authenticated()

                        // All other endpoints are publicly accessible
                        .anyExchange().authenticated()
                )
                // Add custom jwt filter
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf(csrf -> csrf.disable())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
    }
    
}
