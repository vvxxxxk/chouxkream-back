package com.kream.chouxkream.common.config;

import com.kream.chouxkream.common.filter.LoggerFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    @Profile("local")
    public FilterRegistrationBean<Filter> regLoggerFilter() {
        FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<>(new LoggerFilter());
        filterRegistration.setUrlPatterns(List.of("/api/*"));
        filterRegistration.setOrder(0);
        return filterRegistration;
    }

}
