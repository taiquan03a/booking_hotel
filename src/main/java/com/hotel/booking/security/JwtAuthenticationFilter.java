package com.hotel.booking.security;

import com.hotel.booking.constants.JwtConstant;
import com.hotel.booking.repository.TokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected final void doFilterInternal(@NonNull HttpServletRequest request,
                                          @NonNull HttpServletResponse response,
                                          @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(JwtConstant.JWT_HEADER);
        final String jwt;
        final String userEmail;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractEmail(jwt);
        } else {
            if (authHeader != null) {
                logger.error("JWT Token does not begin with Bearer String");
            } else
                filterChain.doFilter(request, response);
            return;
        }
        try {
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails) && tokenRepository.findTokenByExpiredTrueAndRevokedTrueAndToken(jwt).isEmpty()
                        && !jwtService.getRolesFromToken(jwt).isEmpty()) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                else {
                    final String tokenType = jwtService.extractClaim(jwt, claims -> claims.get("token_type", String.class));
                    if (tokenType.equals("refreshToken")){
                        System.out.println("Đã dùng refreshToken vào dc");
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                null, null, null);
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }
        } catch (ExpiredJwtException exception) {
            String isRefreshToken = request.getHeader("refresh_token");
            String requestURL = request.getRequestURL().toString();
            // allow for Refresh Token creation if following conditions are true.
            if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshtoken")) {
                allowForRefreshToken(exception, request);
            } else
                request.setAttribute("exception", exception);
        }
        filterChain.doFilter(request, response);
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {

        // create a UsernamePasswordAuthenticationToken with null values.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        // After setting the Authentication in the context, we specify that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // Set the claims so that in controller we will be using it to create new JWT
        request.setAttribute("claims", ex.getClaims());

    }
}
