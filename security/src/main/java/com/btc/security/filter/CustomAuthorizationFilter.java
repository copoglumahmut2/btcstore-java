package com.btc.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.btc.domain.data.custom.login.JwtUserData;
import com.btc.security.configuration.SecurityConfig;
import com.btc.security.constant.AuthConstants;
import com.btc.security.domain.ErrorObject;
import com.btc.service.SiteService;
import com.btc.service.user.UserGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
                jwtUserData.setAsm(BooleanUtils.toBoolean(decodedJWT.getClaim("asm").asBoolean()));
                jwtUserData.setAsmUsername(decodedJWT.getClaim("asmUsername").asString());
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
            httpServletResponse.setContentType(APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpServletResponse.setHeader(AuthConstants.ERROR, "No token is provided");

            var error = new ErrorObject("No token is provided", HttpServletResponse.SC_UNAUTHORIZED);
            new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), error);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest httpServletRequest) throws ServletException {
        return httpServletRequest.getServletPath().contains("/login") ||
                Arrays.asList(SecurityConfig.excludingSwaggerPaths).contains(httpServletRequest.getServletPath())
                || (StringUtils.isNotEmpty(httpServletRequest.getServletPath()));
    }
}
