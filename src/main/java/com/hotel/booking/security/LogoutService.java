package com.hotel.booking.security;


import com.hotel.booking.model.User;
import com.hotel.booking.repository.UserRepository;
import com.hotel.booking.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String refreshToken;
            final String userEmail;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return;
            }
            refreshToken = authHeader.substring(7);
            userEmail = jwtService.extractEmail(refreshToken);
            if (userEmail != null) {
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow();
                if (jwtService.isTokenValid(refreshToken, user)) {
                    authenticationService.revokeAllUserTokens(user);
                }
            }
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }


    }
}