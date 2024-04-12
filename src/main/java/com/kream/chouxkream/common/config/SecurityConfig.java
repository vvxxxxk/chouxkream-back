package com.kream.chouxkream.common.config;

import com.kream.chouxkream.auth.JwtUtils;
import com.kream.chouxkream.auth.OAuth2SuccessHandler;
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
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Value("${cors.host}")
    private String WEB_HOST;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtils jwtUtils, AuthService authService, OAuth2UserService oAuth2UserService, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtils = jwtUtils;
        this.authService = authService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
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
                        .antMatchers("/all", "/api/**", "/api/auth/**", "/api/users/**").permitAll()
                        .antMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
                        .antMatchers("/user", "/my","/api/search/recent-keyword").hasAnyRole("USER", "SOCIAL")
                        .antMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint());      // ToDo. 권한 없는 페이지 가면 임시로 메인 페이지로 이동하도록

        // 필터 등록
        http
                .addFilterBefore(new JwtLogoutFilter(jwtUtils, authService), LogoutFilter.class);
        http
                .addFilterBefore(new JwtVerificationFilter(jwtUtils), JwtLoginFilter.class);
        http
                .addFilterAt(new JwtLoginFilter(authenticationManager(authenticationConfiguration), jwtUtils, authService), UsernamePasswordAuthenticationFilter.class);

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
