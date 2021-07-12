package se.skoglycke.isitup.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.skoglycke.isitup.api.v1.controller.beans.ServicesResponse;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class CacheConfiguration {

    @Bean
    public Cache<UUID, ServicesResponse> servicesResponseCache(@Value("${is-it-up.cache-expire-after-write-in-minutes:5}") Integer expireAfterWriteInMinutes) {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(expireAfterWriteInMinutes))
                .build();
    }

}
