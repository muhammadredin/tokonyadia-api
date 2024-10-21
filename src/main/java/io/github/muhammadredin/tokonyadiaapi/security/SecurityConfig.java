package io.github.muhammadredin.tokonyadiaapi.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");

        // Configure HTTP security for the application
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .httpBasic(AbstractHttpConfigurer::disable) // Disable basic authentication
                .authorizeRequests(request -> request.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll() // Allow access to error dispatch
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow CORS preflight requests
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll() // Allow login endpoint
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll() // Allow registration endpoint
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh-token").permitAll() // Allow refresh token endpoint
                        .requestMatchers(HttpMethod.POST, "/api/payments/notification")
                        .access("hasIpAddress('34.101.68.130') or hasIpAddress('34.101.92.69') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')") // IP whitelisting for payment notifications
                        .anyRequest().authenticated()) // All other requests require authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session management to stateless
//                .addFilterBefore(ipWhitelistFilter, UsernamePasswordAuthenticationFilter.class) // Uncomment if using IP whitelist filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter before username/password authentication filter
                .exceptionHandling(cfg -> {
                    cfg.authenticationEntryPoint(customAuthenticationEntryPoint); // Custom entry point for authentication errors
                    cfg.accessDeniedHandler(customAccessDeniedHandler); // Custom handler for access denied errors
                })
                .build();
    }
}
