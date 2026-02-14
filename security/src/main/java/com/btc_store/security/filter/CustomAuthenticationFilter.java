package com.btc_store.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.btc_store.domain.data.custom.login.LoginRequest;
import com.btc_store.domain.data.custom.restservice.ServiceResponseData;
import com.btc_store.domain.enums.LoginResultType;
import com.btc_store.domain.enums.ProcessStatus;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.UserAuditModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.security.domain.AuthToken;
import com.btc_store.service.MediaService;
import com.btc_store.service.ModelService;
import com.btc_store.service.ParameterService;
import com.btc_store.service.SiteService;
import com.btc_store.service.constant.ServiceConstant;
import com.btc_store.service.exception.StoreRuntimeException;
import com.btc_store.service.exception.user.UserLockedException;
import com.btc_store.service.user.UserService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import constant.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import util.StoreWebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@Builder
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String LOGIN_REQUEST = "loginRequest";
    private static final String SYSTEM = "SYSTEM";
    protected static String LOGIN_AUTHENTICATION_TYPE_KEY = "login.authentication.type";
    private static final String USER_BLOCKED_TIME_KEY = "login.blocked.time";
    private static final String USER_BLOCKED_MAX_ATTEMPT_KEY = "login.blocked.max.attempt";
    private final AuthenticationManager authenticationManager;

    protected final String secretKey;
    protected final int accessTokenExpire;
    protected final int refreshTokenExpire;
    protected final MessageSource messageSource;
    protected final SiteService siteService;
    protected final UserService userService;
    protected final ModelService modelService;
    protected final MediaService mediaService;
    protected final ParameterService parameterService;


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        var host = request.getHeader(HttpHeaders.HOST);
        var siteModel = siteService.getSiteModelByDomain(StringUtils.split(host, ":")[0]);
        LoginRequest loginRequest;
        if (APPLICATION_JSON_VALUE.equals(request.getHeader(HttpHeaders.CONTENT_TYPE))) {
            loginRequest = this.getLoginRequest(request);
        } else {
            loginRequest = new LoginRequest();
            loginRequest.setUsername(super.obtainUsername(request));
            loginRequest.setPassword(super.obtainPassword(request));
            loginRequest.setSite(siteModel);
        }

        var asmParam = StoreWebUtils.getCurrentHttpRequest().getParameter("asm");


        setBackOffice(request, loginRequest);
        request.setAttribute(LOGIN_REQUEST, loginRequest);
        var authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        setDetails(request, authentication);

        Authentication authenticationResponse = authenticationManager.authenticate(authentication);


        return authenticationResponse;
    }

    private void setBackOffice(HttpServletRequest request, LoginRequest loginRequest) {
        var backofficeUrl = parameterService.getParameterModel(ServiceConstant.SITE_BACKOFFICE_URL, loginRequest.getSite());
        if (StringUtils.contains(backofficeUrl.getValue(), request.getHeader(HttpHeaders.REFERER))) {
            loginRequest.setBackoffice(Boolean.TRUE);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                           HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // provide JWT token
        var ip = request.getHeader("x-real-ip");
        var clientInfo = request.getHeader("User-Agent");
        var host = request.getHeader(HttpHeaders.HOST);
        var user = (User) authResult.getPrincipal();
        var siteModel = siteService.getSiteModelByDomain(StringUtils.split(host, ":")[0]);
        var userModel = userService.getUserModelForBack(user.getUsername(), siteModel);

        Boolean control = Boolean.TRUE;
        if (Objects.nonNull(userModel.getPasswordBlockedDate())) {
            var blockedTimeParameterModel = parameterService.getParameterModel(USER_BLOCKED_TIME_KEY, siteModel);
            var lockExpirationDate = DateUtils.addSeconds(userModel.getPasswordBlockedDate(), Integer.valueOf(blockedTimeParameterModel.getValue()));
            var now = new Date();
            if (userModel.isPasswordBlocked() && BooleanUtils.isTrue(now.before(lockExpirationDate))) {
                try {
                    unsuccessfulAuthentication(request, response, new UserLockedException("User is locked"));
                    control = Boolean.FALSE;
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (BooleanUtils.isTrue(control)) {
            final var algorithm = Algorithm.HMAC256(secretKey.getBytes());
            var jwtId = UUID.randomUUID().toString();
            var accessToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(DateUtils.addMinutes(new Date(), accessTokenExpire))
                    .withIssuer(siteModel.getCode())
                    .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .withJWTId(jwtId)
                    .sign(algorithm);

            var refreshToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(DateUtils.addMinutes(new Date(), refreshTokenExpire))
                    .withIssuer(String.join(ServiceConstant.HYPHEN, siteModel.getCode()))
                    .withJWTId(jwtId)
                    .sign(algorithm);

            response.setContentType(APPLICATION_JSON_VALUE);
            // Login başarılı olursa son başarılı kaydın tarihinin LastLoginDate alanına setlenmesi.
            if (Objects.nonNull(userModel)) {
                userModel.setPasswordBlocked(false);
                userModel.setPasswordBlockedDate(null);
                userModel.setPasswordBlockedAttempt(0);
                userModel.setLastLoginDate(new Date());
                modelService.save(userModel);
            }

            var userGroups = Optional.ofNullable(userModel.getUserGroups()).orElse(new HashSet<>());



            var authToken = AuthToken.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .username(userModel.getUsername())
                    .firstName(userModel.getFirstName())
                    .lastName(userModel.getLastName())
                    .userGroups(userGroups.stream().map(UserGroupModel::getCode).collect(Collectors.toSet()))
                    .picture(mediaService.generateMediaUrl(Objects.nonNull(userModel.getPicture())
                            ? userModel.getPicture().getServePath() : StringUtils.EMPTY))
                    .language(Objects.nonNull(userModel.getLanguage()) ? userModel.getLanguage().getCode() :
                            siteModel.getLanguage().getCode())
                    .build();

            request.setAttribute("authToken", authToken);


            var userAuditModel = modelService.create(UserAuditModel.class);
            userAuditModel.setCode(UUID.randomUUID().toString());
            userAuditModel.setSite(siteModel);
            userAuditModel.setUser(userModel);
            userAuditModel.setLoginDate(new Date());
            userAuditModel.setLoginResult(LoginResultType.SUCCESSFULL);
            userAuditModel.setCreatedDate(new Date());
            userAuditModel.setCreatedBy(SYSTEM);
            userAuditModel.setIp(ip);
            userAuditModel.setClientInfo(clientInfo);
            modelService.save(userAuditModel);

            // Login başarılı, token'ı döndür ve filter chain'e devam etme
            new ObjectMapper().writeValue(response.getOutputStream(), authToken);
        }
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        var loginRequest = (LoginRequest) request.getAttribute("loginRequest");
        var locale = request.getParameter("isoCode");
        locale = StringUtils.isEmpty(locale) ? loginRequest.getSite().getLanguage().getCode() : locale;
        var errorMessage = messageSource.getMessage(MessageConstant.USER_CREDENTIALS_ARE_NOT_VALID, null, Locale.forLanguageTag(locale));
        if (failed.getCause() instanceof StoreRuntimeException) {
            StoreRuntimeException ex = (StoreRuntimeException) failed.getCause();
            errorMessage = messageSource.getMessage(ex.getMessageKey(),
                    ex.getArgs(), Locale.forLanguageTag(locale));
        }
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        var username = loginRequest.getUsername();
        var userModel = userService.getUserModelForStore(username, loginRequest.getSite());
        var resultCheck = StringUtils.EMPTY;
        var siteModel = siteService.getCurrentSite();
        var maxAttemptsParameterModel = parameterService.getParameterModel(USER_BLOCKED_MAX_ATTEMPT_KEY, siteModel);
        if (Objects.nonNull(maxAttemptsParameterModel)) {
            if (Objects.nonNull(userModel)) {
                if (!userModel.isPasswordBlocked()) {
                    var attempts = userModel.getPasswordBlockedAttempt() + 1;
                    userModel.setPasswordBlockedAttempt(attempts);
                    if (attempts >= Integer.valueOf(maxAttemptsParameterModel.getValue())) {
                        userModel.setPasswordBlocked(true);
                        userModel.setPasswordBlockedDate(new Date());
                    }
                    modelService.save(userModel);
                } else {
                    resultCheck = checkAndHandlePasswordLock(userModel, locale, siteModel);
                }
            }
        }
        var serviceResponseData = new ServiceResponseData();
        serviceResponseData.setStatus(ProcessStatus.ERROR);
        serviceResponseData.setErrorMessage(StringUtils.isNotEmpty(resultCheck) ? resultCheck : errorMessage);
        var mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.writeValue(response.getOutputStream(), serviceResponseData);
        var ip = request.getRemoteAddr();
        var clientInfo = request.getHeader("User-Agent");
        try {
            var userAuditModel = modelService.create(UserAuditModel.class);
            userAuditModel.setCode(UUID.randomUUID().toString());
            userAuditModel.setSite(siteModel);
            userAuditModel.setUser(userModel);
            userAuditModel.setLoginDate(new Date());
            userAuditModel.setLoginResult(LoginResultType.UNSUCCESSFULL);
            userAuditModel.setCreatedDate(new Date());
            userAuditModel.setCreatedBy(SYSTEM);
            userAuditModel.setIp(ip);
            userAuditModel.setClientInfo(clientInfo);
            modelService.save(userAuditModel);
        } catch (Exception exception) {
            log.error("userAudit saving error with username: " + username);
        }
    }

    private LoginRequest getLoginRequest(HttpServletRequest request) {
        BufferedReader bufferedReader = null;
        LoginRequest loginRequest = null;
        try {
            bufferedReader = request.getReader();
            loginRequest = new ObjectMapper().readValue(bufferedReader, LoginRequest.class);
            var host = request.getHeader(HttpHeaders.HOST);
            var siteModel = siteService.getSiteModelByDomain(StringUtils.split(host, ":")[0]);
            loginRequest.setSite(siteModel);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException ex) {
                log.error(ex.getLocalizedMessage());
            }
        }
        return loginRequest != null ? loginRequest : new LoginRequest();
    }

    private String checkAndHandlePasswordLock(UserModel userModel, String locale, SiteModel siteModel) {
        var errorMessage = new StringBuilder();
        var now = new Date();
        if (userModel.isPasswordBlocked()) {
            var blockedTimeParameterModel = parameterService.getParameterModel(USER_BLOCKED_TIME_KEY, siteModel);
            var lockExpirationDate = DateUtils.addSeconds(userModel.getPasswordBlockedDate(), Integer.valueOf(blockedTimeParameterModel.getValue()));
            if (now.before(lockExpirationDate)) {
                long remainingSeconds = (lockExpirationDate.getTime() - now.getTime()) / 1000;
                log.error("User login error with username: " + userModel.getUsername());

                errorMessage.append(messageSource.getMessage("user.blocked.message",
                        new Object[]{remainingSeconds}, Locale.forLanguageTag(locale)));
            } else {
                userModel.setPasswordBlocked(false);
                userModel.setPasswordBlockedDate(null);
                userModel.setPasswordBlockedAttempt(1);
                modelService.save(userModel);
            }
        }
        return errorMessage.toString();
    }
}