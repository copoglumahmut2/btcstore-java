package com.btc.security.configuration;

import com.btc.security.filter.*;
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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    protected final UserDetailsService userDetailsService;

    protected final ParameterService parameterService;

    private final SearchService searchService;

    @Value("${authentication.secret.key}")
    protected String secretKey;

    @Value("${authentication.secret.accessTokenExpire}")
    protected int accessTokenExpire;

    @Value("${authentication.secret.refreshTokenExpire}")
    protected int refreshTokenExpire;


    protected final MessageSource messageSource;
    protected final SiteService siteService;
    protected final UserService userService;
    protected final ModelService modelService;
    protected final MediaService mediaService;
    protected final UserGroupService userGroupService;

    public String[] excludingPathsForHttpSec = new String[]{
            "/h2/**",
            "/token/refresh/**",
    };

    public String[] excludingPathsForWebSec = new String[]{
            "/h2/**",
            "/token/refresh/**",
    };

    public static String[] excludingSwaggerPaths = new String[]{
            "/v3/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**,",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    public static String pathPrefix = "/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationFilter authenticationFilter = CustomAuthenticationFilter.builder()
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
                .build();
        
        authenticationFilter.setFilterProcessesUrl("/login");

        CustomAuthorizationFilter authorizationFilter = CustomAuthorizationFilter.builder()
                .secretKey(secretKey)
                .siteService(siteService)
                .userGroupService(userGroupService)
                .build();

        http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2/**")
                )
                .headers(headers -> headers
                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline'; " +
                                "style-src 'self' 'unsafe-inline'; " +
                                "img-src 'self' data:; " +
                                "font-src 'self' data:; " +
                                "object-src 'none';"
                        ))
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ArrayUtils.addAll(excludingPathsForHttpSec, excludingSwaggerPaths)).permitAll()
                        .requestMatchers(HttpMethod.POST, "/token/refresh/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilter(authenticationFilter)
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
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
        return executor;
    }
}
