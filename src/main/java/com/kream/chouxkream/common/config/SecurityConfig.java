package com.kream.chouxkream.common.config;

import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.filter.*;
import com.kream.chouxkream.auth.service.AuthService;
import com.kream.chouxkream.auth.service.OAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티를 통해 웹 보안 설정 활성화
public class SecurityConfig {

    // jwt
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtils jwtUtils;
    private final AuthService jwtService;
    // oauth2
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Value("${cors.host}")
    private String WEB_HOST;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtils jwtUtils, AuthService jwtService, OAuth2UserService oAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtils = jwtUtils;
        this.jwtService = jwtService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf 비활성화 및 cors 설정
        http
                .csrf().disable()
                .cors()
                .configurationSource(corsConfigurationSource());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic().disable();

        // oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

        // 세션 비활성화
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 접근 권한 설정
        // ToDo. 추후 접근 권한 설정 세분화 필요. 임시로 모든 경로에 대해서 허용
        http
                .authorizeHttpRequests((auth) -> auth
                        .antMatchers("/", "/all", "/redis", "/api/**", "/api/user/login", 
                                        "/api/auth/join", "/api/mail/**").permitAll()
                        .antMatchers("/user", "/my").hasAnyRole("USER", "SOCIAL")
                        .antMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());                  

        // 필터 등록
        http
                .addFilterBefore(new JwtLogoutFilter(jwtUtils, jwtService), LogoutFilter.class);
//        http
//                .addFilterBefore(new JwtVerificationFilter(jwtUtils), JwtLoginFilter.class);
        http
                .addFilterBefore(new JWTFilter(jwtUtils), JwtLoginFilter.class);
        http
                .addFilterAt(new JwtLoginFilter(authenticationManager(authenticationConfiguration), jwtUtils, jwtService), UsernamePasswordAuthenticationFilter.class);

        // 폼 로그인 비활성화
        http
                .formLogin().disable()
                .headers().frameOptions().disable();

        return http.build();

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(WEB_HOST)); // cors 허용 url
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 해당 설정 적용

        return source;
    }
}
