//package com.pharmacy.gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
//@Configuration
//public class CorsGlobalConfiguration {
//
//    @Bean
//    public CorsWebFilter corsWebFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOriginPattern("*"); // or specific domains like "http://localhost:3000"
//        config.addAllowedHeader("*"); // allows Authorization, Content-Type, etc.
//        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE, OPTIONS, etc.
//        config.setMaxAge(3600L); // cache preflight response for 1 hour
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsWebFilter(source);
//    }
//}
//



/*
 	Public class WebConfig {
 
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // Adjust the origin as needed
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}
 */

