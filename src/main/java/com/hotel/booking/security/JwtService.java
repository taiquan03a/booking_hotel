package com.hotel.booking.security;



import com.hotel.booking.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${application.secret-key}")
    private String secretKey;

    @Value("${application.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails, User user) {
        Map<String, Object> claims = new HashMap<>(Map.of("role", userDetails.getAuthorities().stream().map(Object::toString).toList()));
        claims.put("token_type", "accessToken");
        claims.put("id", user.getId());
        return buildToken(claims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refreshToken");
        return buildToken(claims, userDetails, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationTime) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        final String tokenType = extractClaim(token, claims -> claims.get("token_type", String.class));
        try {
            if (tokenType.equals("accessToken")) {
                return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
            }
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("Token is expired!");
        }
        return false;
    }

    public boolean isFreshTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        final String tokenType = extractClaim(token, claims -> claims.get("token_type", String.class));
        try {
            if (tokenType.equals("refreshToken")) {
                return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
            }
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw new JwtAuthenticationException("Token is expired!");
        }
        return false;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }


    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getEmailFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return String.valueOf(claims.get("email"));
    }

    public List<?> getRolesFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return (List<?>) claims.get("role");
    }
}
