package se.skoglycke.isitup.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import se.skoglycke.isitup.api.v1.controller.beans.ServicesResponse;
import se.skoglycke.isitup.api.v1.controller.mapper.ServiceMapper;

import java.time.Duration;
import java.util.UUID;

@TestConfiguration
public class ControllerTestConfiguration {

    @Bean
    public Cache<UUID, ServicesResponse> servicesResponseCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    @Bean
    public ServiceMapper serviceMapper() {
        return new ServiceMapper(2000, 5000);
    }

}
