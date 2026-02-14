package com.btc_store.security.configuration;
import com.btc_store.security.filter.CustomAuthenticationFilter;
import com.btc_store.security.filter.CustomAuthorizationFilter;
import com.btc_store.service.*;
import com.btc_store.service.user.UserGroupService;
import com.btc_store.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Qualifier("bambooUserService")
    private final UserDetailsService userDetailsService;

    private final ParameterService parameterService;
    private final SearchService searchService;
    private final MessageSource messageSource;
    private final SiteService siteService;
    private final UserService userService;
    private final ModelService modelService;
    private final MediaService mediaService;
    private final UserGroupService userGroupService;

    @Value("${authentication.secret.key}")
    private String secretKey;

    @Value("${authentication.secret.accessTokenExpire}")
    private int accessTokenExpire;

    @Value("${authentication.secret.refreshTokenExpire}")
    private int refreshTokenExpire;

    public String[] excludingPathsForHttpSec = new String[]{
            "/register/**",
            "/*/register/**",
            "/logout/**",
            "/h2/**",
            "/token/refresh/**",
            "/usersHealth",
            "/reset-password/**",
            "/*/reset-password/**",
            "/password-validity-control",
            "/validate-token/**",
            "/*/validate-token/**",
            "/refresh-token/**",
            "/*/refresh-token/**",
            "/authentication-token-login/**",
            "/*/authentication-token-login/**"};

    public String[] excludingPathsForWebSec = new String[]{
            "/favicon.ico",
            "/register/**",
            "/*/register/**",
            "/h2/**",
            "/usersHealth/**",
            "/reset-password/**",
            "/*/reset-password/**",
            "/password-validity-control",
            "/validate-token/**",
            "/*/validate-token/**",
            "/refresh-token/**",
            "/*/refresh-token/**",
            "/get-filo/**",
            "/*/get-filo/**",
            "/authentication-token-login/**",
            "/*/authentication-token-login/**"};

    public static String[] excludingSwaggerPaths = new String[]{
            "/v3/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    public static String paymentCallbackPath = "/payment-callback";
    public static String paymentCallbackPathWithoutCart = "/payment-callback-without-cart";
    public static String paymentCallbackRemaining = "/payment-callback-remaining";
    
    public static String[] excludingPaymentPaths = new String[]{
            "/payment-callback", 
            "/*/payment-callback",
            "/payment-callback-without-cart", 
            "/*/payment-callback-without-cart",
            "/payment-callback-remaining",
            "/*/payment-callback-remaining"
    };

    public static String[] excludingCaptchaAndConsentPaths = new String[]{
            "/consent/login/**",
            "/*/consent/login/**",
            "/consent/*/login/**",
            "/*/consent/*/login/**"
    };
    
    public static String[] loginPassivePaths = new String[]{
            "/consent/login-passive/**",
            "/*/consent/login-passive/**",
            "/consent/*/login-passive/**",
            "/*/consent/*/login-passive/**"
    };
    
    public static String[] extProcessPaths = new String[]{"/ext-process/**"};
    public static String b2bRegistrationEndPoint = "/b2b-registrations";
    public static String[] b2bRegistrationPath = new String[]{
            "/b2b-registrations",
            "/*/b2b-registrations"
    };
    public static String supplierEndPoint = "/suppliers";
    public static String[] supplierPath = new String[]{
            "/suppliers/**",
            "/*/suppliers/**"
    };

    public static String[] excludingHeartBeatPaths = new String[]{
            "/heart-beat",
            "/*/heart-beat",
            "/sap-service-health-check",
            "/*/sap-service-health-check",
            "/solr-service-health-check",
            "/*/solr-service-health-check",
            "/mail-service-health-check",
            "/*/mail-service-health-check",
            "/tcmb-service-health-check",
            "/*/tcmb-service-health-check"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        String[] allExcludedPaths = ArrayUtils.addAll(
                ArrayUtils.addAll(
                        ArrayUtils.addAll(
                                ArrayUtils.addAll(
                                        ArrayUtils.addAll(
                                                ArrayUtils.addAll(
                                                        ArrayUtils.addAll(
                                                                ArrayUtils.addAll(excludingPathsForHttpSec, excludingSwaggerPaths),
                                                                excludingPaymentPaths),
                                                        excludingCaptchaAndConsentPaths),
                                                excludingHeartBeatPaths),
                                        loginPassivePaths),
                                extProcessPaths),
                        b2bRegistrationPath),
                supplierPath);

        http
                .cors(cors -> cors.configure(http))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self'; object-src 'none';"))
                        .frameOptions(frame -> frame.disable())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allExcludedPaths).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilter(CustomAuthenticationFilter.builder()
                        .secretKey(secretKey)
                        .parameterService(parameterService)
                        .accessTokenExpire(accessTokenExpire)
                        .refreshTokenExpire(refreshTokenExpire)
                        .siteService(siteService)
                        .userService(userService)
                        .modelService(modelService)
                        .mediaService(mediaService)
                        .authenticationManager(authenticationManager)
                        .messageSource(messageSource)
                        .build())
                .addFilterBefore(CustomAuthorizationFilter.builder()
                        .secretKey(secretKey)
                        .siteService(siteService)
                        .userGroupService(userGroupService)
                        .build(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        String[] allIgnoredPaths = ArrayUtils.addAll(
                ArrayUtils.addAll(
                        ArrayUtils.addAll(
                                ArrayUtils.addAll(
                                        ArrayUtils.addAll(
                                                ArrayUtils.addAll(
                                                        ArrayUtils.addAll(
                                                                ArrayUtils.addAll(
                                                                        ArrayUtils.addAll(excludingPathsForHttpSec, excludingSwaggerPaths),
                                                                        excludingPaymentPaths),
                                                                excludingCaptchaAndConsentPaths),
                                                        excludingHeartBeatPaths),
                                                excludingPathsForWebSec),
                                        loginPassivePaths),
                                extProcessPaths),
                        b2bRegistrationPath),
                supplierPath);

        return (web) -> web.ignoring()
                .requestMatchers(allIgnoredPaths)
                .requestMatchers(HttpMethod.POST, "/token/refresh/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new DelegatingSecurityContextAsyncTaskExecutor(threadPoolTaskExecutor);
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return executor;
    }
}
