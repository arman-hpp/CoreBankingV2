package com.bank.core.configs.cache;

import com.bank.core.cache.CacheKeyGenerator;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration implements CachingConfigurer {
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }
}