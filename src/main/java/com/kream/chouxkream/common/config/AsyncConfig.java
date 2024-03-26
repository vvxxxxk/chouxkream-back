package com.kream.chouxkream.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "mailExcutor")
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // 기본으로 실행 대기중인 쓰레드의 수
        executor.setMaxPoolSize(5); // 동시에 동작하는 최대 쓰레드의 수
        executor.setQueueCapacity(500); // CorePool의 크기를 넘어가면 큐에 저장하는데 그 큐의 최대 용량
        executor.setThreadNamePrefix("Async MailExecutor-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler(){
        return AsyncConfigurer.super.getAsyncUncaughtExceptionHandler();
    }
}
