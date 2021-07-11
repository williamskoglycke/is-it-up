package se.skoglycke.isitup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.skoglycke.isitup.domain.service.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class ContextConfiguration {

    @Bean
    public ConcurrentMap<Long, Service> serviceStatusContext() {
        return new ConcurrentHashMap<>();
    }

}
