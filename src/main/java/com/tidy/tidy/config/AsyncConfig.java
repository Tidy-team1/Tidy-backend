package com.tidy.tidy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 피드백 생성용 @Async
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(4);
        exec.setMaxPoolSize(8);
        exec.setQueueCapacity(50);
        exec.setThreadNamePrefix("review-");
        exec.initialize();
        return exec;
    }

    /**
     *
     * @return
     */
    @Bean(name = "modifyExecutor")
    public Executor modifyExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);      // 기본 스레드 수
        executor.setMaxPoolSize(8);       // 최대 스레드 수
        executor.setQueueCapacity(50);   // 큐 용량
        executor.setThreadNamePrefix("modify-"); // 디버깅용 prefix
        executor.initialize();
        return executor;
    }

}
