package com.fc.sns.controller.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/*/users/join", "/api/*/users/login").permitAll()//허용
                .antMatchers("/api/**").authenticated()//그외 권한체크
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션관리 안함
        ;
        // TODO
        //.and()
        //.exceptionHandling()
    }
}
