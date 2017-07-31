package com.sorsix.eventscheduler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.Filter;

import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Spring Security Http Session Configuration for session based security
 */

@EnableOAuth2Client
@Configuration
@EnableWebSecurity
@Import({AuthenticationHandlers.class})
public class SessionSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SessionSecurityConfiguration.class);

    private final UserDetailsService userDetailsService;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AuthenticationSuccessHandler successHandler;

    private final AuthenticationFailureHandler failureHandler;

    private final LogoutSuccessHandler logoutSuccessHandler;

    private final PasswordEncoder passwordEncoder;

    private final OAuth2ClientContext oauth2ClientContext;

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SessionSecurityConfiguration(UserDetailsService userDetailsService,
                                        AuthenticationEntryPoint authenticationEntryPoint,
                                        AuthenticationSuccessHandler successHandler,
                                        AuthenticationFailureHandler failureHandler,
                                        LogoutSuccessHandler logoutSuccessHandler,
                                        PasswordEncoder passwordEncoder,
                                        OAuth2ClientContext oauth2ClientContext,
                                        ApplicationEventPublisher eventPublisher) {
        logger.debug("Configuring Spring Session Security Configuration");
        this.oauth2ClientContext = oauth2ClientContext;
        this.eventPublisher = eventPublisher;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("Configuring HttpSecurity");
        // @formatter:off
        http.csrf().disable()
               .httpBasic().disable().addFilterAfter(ssoFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint);

        http.authorizeRequests()
                .antMatchers("/api/public/**")
                .permitAll();

        http.formLogin()
                .loginProcessingUrl("/api/public/login")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .headers()
                .frameOptions()
                .disable();
        // @formatter:on
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.debug(true);
    }

    private Filter ssoFilter() {
        OAuth2ClientAuthenticationProcessingFilter githubFilter =
                new OAuth2ClientAuthenticationProcessingFilter("/api/public/login/github");
        githubFilter.setApplicationEventPublisher(eventPublisher);
        OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(github(), oauth2ClientContext);
        githubFilter.setRestTemplate(githubTemplate);
        UserInfoTokenServices tokenServices =
                new UserInfoTokenServices(githubResource().getUserInfoUri(), github().getClientId());
        tokenServices.setRestTemplate(githubTemplate);
        githubFilter.setTokenServices(tokenServices);
        return githubFilter;
    }

    @Bean
    @ConfigurationProperties("github.client")
    public AuthorizationCodeResourceDetails github() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("github.resource")
    public ResourceServerProperties githubResource() {
        return new ResourceServerProperties();
    }
}

