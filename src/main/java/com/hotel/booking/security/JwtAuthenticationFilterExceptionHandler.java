package com.hotel.booking.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.booking.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
@Slf4j
public class JwtAuthenticationFilterExceptionHandler  extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilterExceptionHandler.class);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException ee){
            handleExpiredJwtException(request,response);
        }catch(MalformedJwtException me){
            handleMalformedJwtException(request,response);
        }catch (SignatureException | UnsupportedJwtException se){
            handleSignatureException(request,response);
        }
    }
    public void handleExpiredJwtException(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Token đã hết hạn")
                .description(request.getServletPath())
                .timestamp(new Date())
                .build();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(convertToJSON(errorResponse));
        logger.error(errorResponse.getMessage());
    }

    public void handleMalformedJwtException(HttpServletRequest request,HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message("Giá trị token đã bị thay đổi")
                .description(request.getServletPath())
                .timestamp(new Date())
                .build();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(convertToJSON(errorResponse));
        logger.error(errorResponse.getMessage());
    }

    public void handleSignatureException(HttpServletRequest request,HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message("Signature của token không khớp với hệ thống")
                .description(request.getServletPath())
                .timestamp(new Date())
                .build();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(convertToJSON(errorResponse));
        logger.error(errorResponse.getMessage());
    }
    public String convertToJSON(Object object) throws JsonProcessingException {
        if(object == null){
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }


}
