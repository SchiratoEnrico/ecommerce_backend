package com.betacom.ecommerce.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

	private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			String header = request.getHeader("Authorization");
	
	        if (header != null && header.startsWith("Bearer ")) {
	            String token = header.substring(7);
	            String username = jwtService.extractUsername(token);
	
	            if (username != null) {
	                UserDetails user = userDetailsService.loadUserByUsername(username);
	
	                UsernamePasswordAuthenticationToken auth =
	                        new UsernamePasswordAuthenticationToken(
	                                user, null, user.getAuthorities());
	
	                SecurityContextHolder.getContext().setAuthentication(auth);
	            }
	        }
	
	        filterChain.doFilter(request, response);
		}
		catch(ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
	        response.setContentType("application/json");
	        response.getWriter().write("{\"msg\": \"Sessione scaduta. Effettua nuovamente il login.\"}");
	        return; //evita di far esplodere springboot
		}
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
	    return request.getServletPath().startsWith("/auth");
	}
}
