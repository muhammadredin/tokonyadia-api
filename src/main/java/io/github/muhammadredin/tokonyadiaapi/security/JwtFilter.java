package io.github.muhammadredin.tokonyadiaapi.security;

import io.github.muhammadredin.tokonyadiaapi.entity.UserAccount;
import io.github.muhammadredin.tokonyadiaapi.service.JwtService;
import io.github.muhammadredin.tokonyadiaapi.service.RedisService;
import io.github.muhammadredin.tokonyadiaapi.service.UserAccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserAccountService userAccountService;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION); // Extract Bearer token from request header
        String token = parseToken(bearerToken); // Parse the token from the Bearer string

        // Refresh token should be verified against the access token provided

        try {
            if (token != null && jwtService.validateToken(token)) { // Validate the token
                if (redisService.get("blacklistToken:" + token) != null) {
                    log.warn("Invalid token: {}", token); // Log warning if token is blacklisted
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
                }

                String userId = jwtService.getUserId(token); // Extract user ID from the token
                UserAccount userAccount = userAccountService.getOne(userId); // Retrieve user account using the user ID

                // Create an authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userAccount,
                        null,
                        userAccount.getAuthorities());

                // Set request details for the authentication object
                authentication.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication); // Set the authentication in the security context
                log.info("User authentication set for user: {}", userAccount.getUsername()); // Log successful authentication
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage()); // Log any exceptions that occur
        } finally {
            filterChain.doFilter(request, response); // Continue the filter chain
        }
    }

    private String parseToken(String bearer) {
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // Return the token part of the Bearer string
        }
        return null; // Return null if the token is not present
    }
}
