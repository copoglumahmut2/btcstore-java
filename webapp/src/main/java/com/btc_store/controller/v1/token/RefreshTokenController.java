package com.btc_store.controller.v1.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.btc_store.domain.data.custom.login.LoginRequest;
import com.btc_store.domain.data.custom.login.RefreshTokenInputData;
import com.btc_store.security.constant.AuthConstants;
import com.btc_store.security.domain.AuthToken;
import com.btc_store.security.domain.ErrorObject;
import com.btc_store.service.MediaService;
import com.btc_store.service.ParameterService;
import com.btc_store.service.SiteService;
import com.btc_store.service.constant.ServiceConstant;
import com.btc_store.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static com.btc_store.security.filter.CustomAuthenticationFilter.LOGIN_REQUEST;

@RestController("refreshTokenControllerV1")
@RequestMapping(AuthConstants.VERSION_V1 + AuthConstants.REFRESH_TOKEN)
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenController {

    @Value("${authentication.secret.key}")
    protected String secretKey;

    @Value("${authentication.secret.accessTokenExpire}")
    protected int accessTokenExpire;

    @Value("${authentication.secret.refreshTokenExpire}")
    protected int refreshTokenExpire;

    protected final UserService userService;

    protected final SiteService siteService;

    protected final MediaService mediaService;

    protected final ParameterService parameterService;


    @PostMapping(AuthConstants.REFRESH)
    public Object refresh(@Validated @RequestBody RefreshTokenInputData refreshTokenInputData,
                          HttpServletResponse response, HttpServletRequest request) {

        final var refreshToken = refreshTokenInputData.getRefreshToken();
        final var algorithm = Algorithm.HMAC256(secretKey.getBytes());
        var verifier = JWT.require(algorithm).build();
        try {
            var refreshTokenDecoded = verifier.verify(refreshToken);
            var host = request.getHeader(HttpHeaders.HOST);
            var siteModel = siteService.getSiteModelByDomain(StringUtils.split(host, ":")[0]);

            var loginRequest = new LoginRequest();
            loginRequest.setSite(siteModel);
            var backofficeUrl = parameterService.getParameterModel(ServiceConstant.SITE_BACKOFFICE_URL, siteModel);
            if (StringUtils.contains(backofficeUrl.getValue(), request.getHeader(HttpHeaders.REFERER))) {
                loginRequest.setBackoffice(Boolean.TRUE);
            }
            request.setAttribute(LOGIN_REQUEST, loginRequest);

            var userModel = userService.loadUserByUsername(refreshTokenDecoded.getSubject());

            var username = refreshTokenDecoded.getSubject();
            var userModelData = userService.getUserModelForBack(username, siteModel);

            var jwtId = UUID.randomUUID().toString();
            var accessToken = JWT.create()
                    .withSubject(username)
                    .withExpiresAt(DateUtils.addMinutes(new Date(), accessTokenExpire))
                    .withIssuer(siteModel.getCode())
                    .withClaim("roles", userModel.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .withClaim("username", userModelData.getUsername())
                    .withClaim("firstName", userModelData.getFirstName())
                    .withClaim("lastName", userModelData.getLastName())
                    .withClaim("picture", mediaService.generateMediaUrl(Objects.nonNull(userModelData.getPicture())
                            ? userModelData.getPicture().getServePath() : StringUtils.EMPTY))
                    .withClaim("language", Objects.nonNull(userModelData.getLanguage()) ? userModelData.getLanguage().getCode() :
                            siteModel.getLanguage().getCode())
                    .withJWTId(jwtId)
                    .sign(algorithm);

            var newRefreshToken = JWT.create()
                    .withSubject(username)
                    .withExpiresAt(DateUtils.addMinutes(new Date(), refreshTokenExpire))
                    .withIssuer(String.join(ServiceConstant.HYPHEN, siteModel.getCode()))
                    .withJWTId(jwtId)
                    .sign(algorithm);

            response.setContentType(APPLICATION_JSON_VALUE);

            return AuthToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken)
                    .build();
        } catch (RuntimeException ex) {
            log.error("New access token could not be generated. Refresh token: [{}]", refreshToken);
        }
        return new ErrorObject("Refresh token is not valid", HttpServletResponse.SC_BAD_REQUEST);
    }
}
