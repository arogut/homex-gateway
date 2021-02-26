package com.arogut.homex.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class AuthSecurityConfig implements WebFluxConfigurer {

    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/h2-console",
            // -- actuator
            "/actuator/**"
    };

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) {
        return http
                .headers()
                .frameOptions().disable()
                .and()
                .authorizeExchange()
                .pathMatchers(AUTH_WHITELIST).permitAll()
                .pathMatchers("/devices/auth/**").permitAll()
                .anyExchange().permitAll()
                .and()
                .httpBasic()
                .and()
                .cors()
                .disable()
                .csrf()
                .disable()
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin").password("admin").roles("USER","ADMIN").build();
        return new MapReactiveUserDetailsService(admin);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
    }
}
