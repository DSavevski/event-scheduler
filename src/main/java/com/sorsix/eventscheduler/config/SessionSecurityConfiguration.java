package com.sorsix.eventscheduler.config;

import com.sorsix.eventscheduler.domain.enums.Provider;
import com.sorsix.eventscheduler.domain.enums.Role;
import com.sorsix.eventscheduler.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.Filter;

import org.springframework.web.filter.CompositeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security Http Session Configuration for session based security
 */

@EnableOAuth2Client
@Configuration
@EnableWebSecurity
@Import({AuthenticationHandlers.class})
public class SessionSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SessionSecurityConfiguration.class);

    private final UserService userService;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    //private final AuthenticationSuccessHandler successHandler;

    //private final AuthenticationFailureHandler failureHandler;

    private final LogoutSuccessHandler logoutSuccessHandler;

    private final PasswordEncoder passwordEncoder;

    private final OAuth2ClientContext oauth2ClientContext;

    //private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SessionSecurityConfiguration(UserService userService,
                                        AuthenticationEntryPoint authenticationEntryPoint,
                                        LogoutSuccessHandler logoutSuccessHandler,
                                        PasswordEncoder passwordEncoder,
                                        OAuth2ClientContext oauth2ClientContext) {
        logger.debug("Configuring Spring Session Security Configuration");
        this.oauth2ClientContext = oauth2ClientContext;
        //this.eventPublisher = eventPublisher;
        this.userService = userService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        //this.successHandler = successHandler;
        //this.failureHandler = failureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.debug("Configuring HttpSecurity");
        // @formatter:off
        http.csrf().disable()
                .httpBasic().disable().addFilterAfter(ssoFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling();
        //.authenticationEntryPoint(authenticationEntryPoint);

        http.authorizeRequests()
                .antMatchers("/api/public/**")
                .permitAll();

        http.formLogin()
                .loginProcessingUrl("/api/public/login")
                .successHandler(localSuccessHandler())
                .failureHandler(loginFailureHandler())
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

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //web.debug(true);
    }

    private Filter ssoFilter() {

        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        OAuth2ClientAuthenticationProcessingFilter facebookFilter = new OAuth2ClientAuthenticationProcessingFilter("/api/public/login/facebook");
        //facebookFilter.setApplicationEventPublisher(eventPublisher);
        OAuth2RestTemplate facebookTemplate = new OAuth2RestTemplate(facebook(), oauth2ClientContext);
        facebookFilter.setRestTemplate(facebookTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(facebookResource().getUserInfoUri(), facebook().getClientId());
        tokenServices.setRestTemplate(facebookTemplate);
        facebookFilter.setTokenServices(tokenServices);
        facebookFilter.setAuthenticationSuccessHandler(facebookSuccessHandler());
        filters.add(facebookFilter);

        OAuth2ClientAuthenticationProcessingFilter githubFilter =
                new OAuth2ClientAuthenticationProcessingFilter("/api/public/login/github");
        //githubFilter.setApplicationEventPublisher(eventPublisher);
        OAuth2RestTemplate githubTemplate = new OAuth2RestTemplate(github(), oauth2ClientContext);
        githubFilter.setRestTemplate(githubTemplate);
        tokenServices =
                new UserInfoTokenServices(githubResource().getUserInfoUri(), github().getClientId());
        tokenServices.setRestTemplate(githubTemplate);
        githubFilter.setTokenServices(tokenServices);
        githubFilter.setAuthenticationSuccessHandler(githubSuccessHandler());
        filters.add(githubFilter);

        filter.setFilters(filters);
        return filter;
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

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("facebook.client")
    public AuthorizationCodeResourceDetails facebook() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("facebook.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }

    @Bean
    public LoginSuccessHandler localSuccessHandler() {
        return new LoginSuccessHandler(Provider.LOCAL, Role.USER);
    }

    @Bean
    LoginSuccessHandler githubSuccessHandler() {
        return new LoginSuccessHandler(Provider.GITHUB, Role.USER);
    }

    @Bean
    LoginSuccessHandler facebookSuccessHandler() {
        return new LoginSuccessHandler(Provider.FACEBOOK, Role.USER);
    }

}


