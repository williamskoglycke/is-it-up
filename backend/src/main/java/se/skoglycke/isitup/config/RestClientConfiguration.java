package se.skoglycke.isitup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class RestClientConfiguration {

    @Bean
    public WebClient webClient(@Value("${is-it-up.ping-timeout:3000}") Long timeoutInMillis) {
        HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofMillis(timeoutInMillis));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
