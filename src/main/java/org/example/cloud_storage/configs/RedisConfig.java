package org.example.cloud_storage.configs;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

@Configuration
@AllArgsConstructor
@EnableCaching
@EnableRedisIndexedHttpSession
public class RedisConfig {

    @Bean
    @Primary
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration("localhost", 6379);
        configuration.setPassword("");
        System.out.println("LettuceConnectionFactory initialized");
        return new LettuceConnectionFactory(configuration);
    }
}