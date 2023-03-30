package com.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author peng
 * @date 2023-03-30 15:25
 */
@Configuration
@EnableAsync
public class ThreadPoolExecutorConfig {

    @Bean(name="threadPoolExecutor")
    public Executor threadPoolExecutor(){
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        // 返回可用处理器的Java虚拟机的数量
        int processNum = Runtime.getRuntime().availableProcessors();
        int corePoolSize = (int) (processNum / (1 - 0.2));
        int maxPoolSize = (int) (processNum / (1 - 0.5));
        // 核心池大小
        threadPoolExecutor.setCorePoolSize(corePoolSize);
        // 最大线程数
        threadPoolExecutor.setMaxPoolSize(maxPoolSize);
        // 队列程度
        threadPoolExecutor.setQueueCapacity(maxPoolSize * 1000);
        threadPoolExecutor.setThreadPriority(Thread.MAX_PRIORITY);
        threadPoolExecutor.setDaemon(false);
        // 线程空闲时间
        threadPoolExecutor.setKeepAliveSeconds(300);
        // 线程名字前缀
        threadPoolExecutor.setThreadNamePrefix("shortLink-Executor-");
        return threadPoolExecutor;
    }
}