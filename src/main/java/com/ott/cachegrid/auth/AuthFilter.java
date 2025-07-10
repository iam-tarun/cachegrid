package com.ott.cachegrid.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.List;

@Component
@Order(1) // this makes the filter run early in the filter chain
public class AuthFilter extends OncePerRequestFilter {

    private final APIKeyService apiKeyService;

    AuthFilter(APIKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    private static final String HDR_ID = "X-PROJECT-ID";
    private static final String HDR_KEY = "X-SECRET-KEY";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String projectID = request.getHeader(HDR_ID);
        String key = request.getHeader(HDR_KEY);

        if (projectID == null || key == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing auth headers");
            return;
        }

        if (!this.apiKeyService.validateApiKey(projectID, key)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return;
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                projectID,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_PROJECT"))
        );

        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);

    }

}
