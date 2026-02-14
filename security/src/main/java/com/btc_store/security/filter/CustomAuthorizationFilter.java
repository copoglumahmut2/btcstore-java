package com.btc_store.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.btc_store.domain.data.custom.login.JwtUserData;
import com.btc_store.security.configuration.SecurityConfig;
import com.btc_store.security.constant.AuthConstants;
import com.btc_store.security.domain.ErrorObject;
import com.btc_store.service.SiteService;
import com.btc_store.service.user.UserGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
@Builder
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    protected String secretKey;
    protected SiteService siteService;
    protected UserGroupService userGroupService;

    protected final static String AUTH_TOKEN_PARAM_KEY = "authtoken";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        var authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                final var userToken = authorizationHeader.substring("Bearer ".length());
                final var algorithm = Algorithm.HMAC256(secretKey.getBytes());

                var verifier = JWT.require(algorithm).build();
                var decodedJWT = verifier.verify(userToken);
                var username = decodedJWT.getSubject();
                var siteModel = siteService.getSiteModel(decodedJWT.getIssuer());

                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

                Stream.of(roles).forEach(role -> userGroupService.getUserGroupModel(role, siteModel).getUserRoles()
                        .forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getCode()))));

                //Stream.of(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                var token = new UsernamePasswordAuthenticationToken(username, null, authorities);
                var jwtUserData = new JwtUserData();
                jwtUserData.setJwtId(decodedJWT.getId());

                jwtUserData.setSite(siteModel);
                token.setDetails(jwtUserData);
                SecurityContextHolder.getContext().setAuthentication(token);
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            } catch (Exception ex) {
                log.error("Error logging in: {}", ex.getMessage());
                httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.setHeader(AuthConstants.ERROR, ex.getMessage());

                var error = new ErrorObject(ex.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
                new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), error);
            }
        } else {
            // Token yoksa da filtreyi geçir, SecurityFilterChain zaten yetkisiz istekleri engelleyecek
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest httpServletRequest) throws ServletException {
        String path = httpServletRequest.getServletPath();
        
        // Login path'i filtreden geçirme
        if (path.contains("/login")) {
            return true;
        }
        
        // Swagger path'lerini kontrol et
        for (String excludedPath : SecurityConfig.excludingSwaggerPaths) {
            String pattern = excludedPath.replace("/**", "").replace("/*", "");
            if (path.startsWith(pattern) || path.contains(pattern)) {
                return true;
            }
        }
        
        return false;
    }
}
