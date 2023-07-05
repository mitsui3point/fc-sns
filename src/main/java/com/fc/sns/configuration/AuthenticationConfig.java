package com.fc.sns.configuration;

import com.fc.sns.configuration.filter.JwtTokenFilter;
import com.fc.sns.exception.CustomAuthenticationEntryPoint;
import com.fc.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret-key}")
    private String key;

    private final UserService userService;


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .regexMatchers("^(?!/api/).*")
                .antMatchers("/api/*/users/join", "/api/*/users/login")//허용, 아예 무시
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()//그외 권한체크
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//세션관리 안함
                .and()
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class)//jwt 토큰 체크 필터 추가
                .exceptionHandling()//filter 에서 에러가 났을 경우
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())//엔트리 포인트로 보내줘야 함
        ;
    }
}
