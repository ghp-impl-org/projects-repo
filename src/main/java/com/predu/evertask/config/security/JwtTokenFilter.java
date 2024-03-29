package com.predu.evertask.config.security;

import com.predu.evertask.domain.model.Role;
import com.predu.evertask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Collection;
import java.util.List;

import static java.util.List.of;
import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();

        if (!jwtTokenUtil.validate(token)) {
            chain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = userRepository
                .findById(jwtTokenUtil.getUserId(token))
                .orElse(null);

        Collection<? extends GrantedAuthority> authorities = jwtTokenUtil.isAuthenticated(token)
                ? ofNullable(userDetails).map(UserDetails::getAuthorities).orElse(of())
                : List.of(new SimpleGrantedAuthority(Role.ROLE_PRE_VERIFICATION_USER));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        authentication
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
