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
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = parseToken(bearerToken);

        // TODO: cek cookie apakah user memiliki refresh token dan buat isRefreshTokenValid untuk memverifikasi
        // refresh token yang dibawa adalah refresh token dari access token yang dibawa

        try {
            if (token != null && jwtService.validateToken(token)) {
                if (redisService.get("blacklistToken:" + token) != null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");

                String userId = jwtService.getUserId(token);
                UserAccount userAccount = userAccountService.getOne(userId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userAccount,
                        null,
                        userAccount.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }

    }

    private String parseToken(String bearer) {
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
