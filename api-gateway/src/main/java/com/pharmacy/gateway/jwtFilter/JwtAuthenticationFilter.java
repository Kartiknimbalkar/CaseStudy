package com.pharmacy.gateway.jwtFilter;

import com.pharmacy.gateway.jwtUtil.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    	
    	// new code added
    	if (exchange.getRequest().getMethod() == org.springframework.http.HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }
    	

        // Retrieve authentication header from the request
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        //  Check if the header is present and starts with Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // extract token from the header excluding Bearer
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtUtil.extractClaims(token);

                // check if the token is expired
                if (!jwtUtil.isTokenExpired(claims)) {

                    // extract username and role from the token
                    String username = jwtUtil.getUsername(claims);
                    String role = jwtUtil.getRoles(claims);

                    // create list of a roles for the user
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                    log.info("{}", authorities);
                    
                    // create authentication token using username and password
                    var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContext securityContext = new SecurityContextImpl(authentication);

                    log.info("Username {} with role {} is authenticated", username, role);

                    // pass this filter and continue with the filter chain
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                } else {
                    log.warn("The JWT token has been expired for the token {}", token);
                }
            } catch (Exception e) {
                log.error("Invalid JWT Token : {}", token, e);

                // return an error response with message
                return Mono.error(new Exception("Invalid JWT token"));
            }
        }

        // if no token is found or invalid, then continue with filter chain without custom filter
        return chain.filter(exchange);
    }
}
