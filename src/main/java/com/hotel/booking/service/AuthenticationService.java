package com.hotel.booking.service;


import com.hotel.booking.dto.auth.AuthenticationRequest;
import com.hotel.booking.dto.auth.RegisterRequest;
import com.hotel.booking.dto.auth.UpdatePasswordRequest;
import com.hotel.booking.model.User;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.security.Principal;

public interface AuthenticationService {
    ResponseEntity<?> authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response, Authentication authentication) throws java.io.IOException;
    ResponseEntity<?> register(RegisterRequest request);
    ResponseEntity<?> updatePassword(UpdatePasswordRequest updatePasswordRequest, Principal connectedUser);
    void revokeAllUserTokens(User user);
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, java.io.IOException;

}
