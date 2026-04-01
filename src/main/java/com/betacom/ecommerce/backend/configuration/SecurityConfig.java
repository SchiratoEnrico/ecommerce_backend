package com.betacom.ecommerce.backend.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.betacom.ecommerce.backend.security.JwtFilter;


import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
//abilito sicurezza dei metodi con annotazione @PreAuthorize
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtFilter jwtFilter;

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoint sempre pubblici per tutti (anche POST)
                .requestMatchers(
                    "/auth/login", 
                    "/rest/account/create", 
                    "/error",
                    "/v3/api-docs/**", 
                    "/swagger-ui/**", 
                    "/swagger-ui.html"
                ).permitAll()
                // Rendiamo pubbliche TUTTE le GET (letture) relative al catalogo!
                .requestMatchers(HttpMethod.GET, 
                		// Manga
                        "/rest/manga/list", 
                        "/rest/manga/find_by_isbn",
                        
                        // Generi (Categorie per i filtri)
                        "/rest/genere/list",
                        "/rest/genere/findById",
                        
                        // Autori
                        "/rest/autore/list",
                        "/rest/autore/findById",
                        
                        // Case Editrici
                        "/rest/casa_editrice/list",
                        "/rest/casa_editrice/findById",
                        
                        // Saghe (se hai il controller per le saghe)
                        "/rest/saga/list",
                        "/rest/saga/findById"
                ).permitAll()
                // Tutto il resto richiede di essere loggati
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
	
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
