package com.greenfoxacademy.islandfoxtribes.security.Jwt;

import com.greenfoxacademy.islandfoxtribes.models.player.Player;
import com.greenfoxacademy.islandfoxtribes.repositories.player.PlayerRepository;
import com.greenfoxacademy.islandfoxtribes.security.OAuth2.CustomOAuth2User;
import com.greenfoxacademy.islandfoxtribes.services.player.UserDetailsServiceForJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.greenfoxacademy.islandfoxtribes.security.SecurityConfigurer.LIST_OAUTH_USERS;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsServiceForJwt userDetailsServiceForJwt;
    private final JwtUtil jwtUtil;
    private final PlayerRepository repository;
    public static String CURRENT_TOKEN;

    @Autowired
    public static CustomOAuth2User oauth;

    @Autowired
    public JwtRequestFilter(UserDetailsServiceForJwt userDetailsServiceForJwt, JwtUtil jwtUtil,
                            PlayerRepository repository) {
        this.userDetailsServiceForJwt = userDetailsServiceForJwt;
        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7).trim();
            CURRENT_TOKEN = jwt;
            username = jwtUtil.extractUsername(jwt);
        }

        if (authorizationHeader != null && username == null && authorizationHeader.length() > 200) {
            Map<String, Object> attributesMap = LIST_OAUTH_USERS.get(0).getAttributes();
            username = attributesMap.get("email").toString();
            UserDetails userDetails = userDetailsServiceForJwt.loadUserByUsername(username);
            jwt = jwtUtil.generateToken(userDetails).getToken();
            CURRENT_TOKEN = jwt;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsServiceForJwt.loadUserByUsername(username);
            Optional<Player> verification = repository.findByUserName(username);
            if (verification.isPresent() && verification.get().isEnabled()) {
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } else {
                throw new ServletException("You must verify your account first. Please check your email.");
            }
        }
        filterChain.doFilter(request, response);
    }
}
