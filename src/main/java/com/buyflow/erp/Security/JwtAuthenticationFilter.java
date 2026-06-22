package com.buyflow.erp.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);
        String uri = request.getRequestURI();
        // /roles/ 요청에만 로그 (OPTIONS 프리플라이트 제외) → 콘솔 폭주 방지
        boolean debug = uri.contains("/roles/") && !"OPTIONS".equalsIgnoreCase(request.getMethod());

        if (debug) {
            System.out.println(">>> [요청 경로] " + uri
                    + " / 토큰 있음? " + StringUtils.hasText(token));
        }

        if (StringUtils.hasText(token)) {
            try {
                jwtTokenProvider.isValid(token);
                String loginId = jwtTokenProvider.getLoginId(token);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                jwtTokenProvider.getRoles(token)
                        .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                jwtTokenProvider.getPermissions(token)
                        .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (debug) {
                    System.out.println(">>> [JWT 검증 성공] loginId=" + loginId
                            + " / authorities=" + authorities);
                }
            } catch (RuntimeException e) {
                if (debug) {
                    System.out.println(">>> [JWT 검증 실패] "
                            + e.getClass().getName() + ": " + e.getMessage());
                }
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        return null;
    }
}