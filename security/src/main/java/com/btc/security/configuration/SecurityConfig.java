package com.btc.security.configuration;

import com.btc.security.filter.*;
import com.btc.service.*;
import com.btc.service.user.UserGroupService;
import com.btc.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .headers().xssProtection().block(true).and()
                .contentSecurityPolicy("default-src 'self'; script-src 'self'; object-src 'none';")
                .and().frameOptions().disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(excludingPathsForHttpSec, excludingSwaggerPaths)))))))))
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().disable()
                .addFilter(CustomAuthenticationFilter.builder().secretKey(secretKey)
                        .parameterService(parameterService)
                        .accessTokenExpire(accessTokenExpire)
                        .refreshTokenExpire(refreshTokenExpire)
                        .siteService(siteService)
                        .userService(userService)
                        .modelService(modelService)
                        .mediaService(mediaService)
                        .authenticationManager(super.authenticationManager()).messageSource(messageSource).build())
                .addFilterBefore(CustomAuthorizationFilter.builder()
                        .secretKey(secretKey)
                        .siteService(siteService)
                        .userGroupService(userGroupService)
                        .build(), UsernamePasswordAuthenticationFilter.class)
        ;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
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


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(ArrayUtils.addAll(excludingPathsForHttpSec, excludingSwaggerPaths)))), excludingPathsForWebSec))))))
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.POST, "/token/refresh/**");
    }
}
