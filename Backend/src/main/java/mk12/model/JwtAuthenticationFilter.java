package mk12.model;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import mk12.service.JwtService;

import java.io.IOException;

/**
 * Filter for JWT authentication.
 * Intercepts HTTP requests to validate JWT tokens and set the authentication context.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /**
     * Constructs a JwtAuthenticationFilter with the specified JwtService.
     *
     * @param jwtService the JwtService to use for token operations
     */
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Filters incoming HTTP requests to validate JWT tokens.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtService.resolveToken(request);

        if (token != null && jwtService.validateToken(token)) {
            String email = jwtService.getEmailFromToken(token);
            SecurityContextHolder.getContext()
                    .setAuthentication(jwtService.getAuthentication(email));
        }

        filterChain.doFilter(request, response);
    }
}