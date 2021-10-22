package net.chinhung.springsecurity.unittesting.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationString = request.getHeader("Authorization");

        if (!jwtUtil.isJwtFormat(authorizationString)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = jwtUtil.loadAuthentication(authorizationString);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
        }

        filterChain.doFilter(request, response);
    }
}
