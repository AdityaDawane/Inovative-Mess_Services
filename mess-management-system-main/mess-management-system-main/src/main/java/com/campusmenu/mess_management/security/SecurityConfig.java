package com.campusmenu.mess_management.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SPRING SECURITY CONFIGURATION
 * Defines which routes are public vs protected
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor

public class SecurityConfig {
    /*

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // ← ALLOW EVERYTHING
            );

    // NO JWT FILTER - testing without it

        return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("*"));
    config.setAllowedMethods(List.of("*"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}


// REMEMBER: This is for testing only!
// Once login works, re-enable security!
}

     */





    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC ROUTES
                        .requestMatchers(
                                "/api/customer/register",
                                "/api/customer/login",
                                "/api/vendor/login",
                                "/api/admin/login",
                                "/api/vendor/menu/**",
                                "/api/public/**",
                                "/api/vendor/session-qr",
                                "/api/vendor/scan-qr",
                                "/api/vendor/meal-plans",        // ✅ ADD THIS
                                "/api/vendor/meal-plans/**",
                                "/api/vendor/all",              // List all vendors (for student selection)
                                "/api/vendor/meal-plans",       // View meal plans
                                "/api/vendor/meal-plans/**",
                                "/api/vendor/menu"

                        ).permitAll()


                        // CUSTOMER ROUTES
                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")

                        // VENDOR ROUTES
                        .requestMatchers("/api/vendor/**").hasRole("VENDOR")

                        // ADMIN ROUTES
                        .requestMatchers("/api/admin/meal-plans/**").hasAnyRole("ADMIN", "VENDOR")

                        // PAYMENT ROUTES
                        .requestMatchers("/api/payment/**").hasAnyRole("CUSTOMER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:4200",
                "http://localhost:8000",
                "http://localhost:5500",   // ← VS Code Live Server
                "http://127.0.0.1:5500",
                "http://localhost:63342",
                "http://127.0.0.1:8000",
                "http://localhost:8081"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}


