package io.github.muhammadredin.tokonyadiaapi.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
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
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Create a list of whitelisted IPs
        IPWhitelistFilter ipWhitelistFilter = new IPWhitelistFilter(
                Arrays.asList("34.101.68.130", "34.101.92.69", "127.0.0.1", "0:0:0:0:0:0:0:1")
        );

        // Add the custom filter to the security chain
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeRequests(request -> request.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/notification")
                        .access("hasIpAddress('34.101.68.130') or hasIpAddress('34.101.92.69') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(ipWhitelistFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(cfg -> {
                    cfg.authenticationEntryPoint(customAuthenticationEntryPoint);
                    cfg.accessDeniedHandler(customAccessDeniedHandler);
                })
                .build();
    }
}
