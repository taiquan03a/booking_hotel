package com.hotel.booking.configuration;

import com.hotel.booking.security.JwtAuthenticationEntryPoint;
import com.hotel.booking.security.JwtAuthenticationFilter;
import com.hotel.booking.security.JwtAuthenticationFilterExceptionHandler;
import com.hotel.booking.security.LogoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;


import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration{

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationFilterExceptionHandler jwtAuthenticationFilterExceptionHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final LogoutService logoutService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        return http
                .cors(withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            new AntPathRequestMatcher("/api/v1/auth/register/**"),
                            new AntPathRequestMatcher("/api/v1/auth/register"),
                            new AntPathRequestMatcher("/api/v1/user/**"),
                            new AntPathRequestMatcher("/api/v1/auth/login"),
                            new AntPathRequestMatcher("/api/v1/auth/refresh"),
                            new AntPathRequestMatcher("/api/v1/room/getByRank/**"),
                            new AntPathRequestMatcher("/api/v1/rank","GET"),
                            new AntPathRequestMatcher("/api/v1/service_hotel/category/get-service"),
                            new AntPathRequestMatcher("/api/v1/zalopay/**")

                )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }

}
