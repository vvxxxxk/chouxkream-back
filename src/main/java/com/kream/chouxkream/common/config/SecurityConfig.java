package com.kream.chouxkream.common.config;

import com.kream.chouxkream.jwt.JwtUtils;
import com.kream.chouxkream.jwt.filter.JwtLogoutFilter;
import com.kream.chouxkream.jwt.filter.JwtVerificationFilter;
import com.kream.chouxkream.jwt.filter.JwtLoginFilter;
import com.kream.chouxkream.jwt.service.JwtService;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity  // 스프링 시큐리티를 통해 웹 보안 설정 활성화
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtils jwtUtils;
    private final JwtService jwtService;

    @Value("${cors.host}")
    private String WEB_HOST;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtUtils jwtUtils, JwtService jwtService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtils = jwtUtils;
        this.jwtService = jwtService;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf 비활성화 및 cors 설정
        http
                .csrf().disable()
                .cors()
                .configurationSource(corsConfigurationSource());

        // 세션 비활성화
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 접근 권한 설정
        // ToDo. 추후 접근 권한 설정 세분화 필요. 임시로 모든 경로에 대해서 허용
        http
                .authorizeHttpRequests((auth) -> auth
                        .antMatchers("/", "/all", "/redis", "/api/**").permitAll()
                        .antMatchers("/user").hasRole("USER")               // 임시. 권한 테스트용
                        .antMatchers("/admin").hasRole("ADMIN"));           // 임시. 권한 테스트용

        // 필터 등록
        http
                .addFilterBefore(new JwtLogoutFilter(jwtUtils, jwtService), LogoutFilter.class);
        http
                .addFilterBefore(new JwtVerificationFilter(jwtUtils), JwtLoginFilter.class);
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
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 해당 설정 적용

        return source;
    }
}
