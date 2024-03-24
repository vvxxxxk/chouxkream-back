package com.kream.chouxkream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()// 로그인 페이지와 기타 로그인 처리 및 성공 실패를 사용
                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 스프링 시큐리티가 세션정책을 생성하지도 않고 기존 것을 사용하지도 않음 -> jwt방식을 쓸때 사용
                .and()
                .authorizeRequests()
                .antMatchers("/api/user/login").permitAll() //antmatchers ->특정 리소스의 권한 설정
                .antMatchers("/api/user/join").permitAll() //permitall -> 설정 리소스에 대한 접근을 인증없이 허용
                .antMatchers("/api/mail/**").permitAll() // 회원가입 -> 메일 인증 관련 url
                .antMatchers("/api/user").hasRole("USER") // user로 시작하는 모든 url은 인증후 유저레벨의 권한을 가진 사용자만 접근을 허용
                .anyRequest().authenticated(); //모든 리소스를 의미하며 접근허용 리소스 및 인증후 특정 레벨의 권한을 가진 사용자만 접근가능한 리소스를 설정하고 그외 나머지 리소스들은 무조건 인증을 완료해야 접근이 가능
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/file/**",
            "/image/**",
            "/swagger/**",
            "/swagger-ui/**",
            "/h2/**"
    };

    // 정적인 파일 요청에 대해 무시
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }
}