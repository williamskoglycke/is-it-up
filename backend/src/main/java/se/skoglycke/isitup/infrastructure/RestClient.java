package se.skoglycke.isitup.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

    private final WebClient webClient;
    private final Long timeoutInMillis;

    public RestClient(WebClient webClient,
                      @Value("${is-it-up.ping-timeout:3000}") Long timeoutInMillis) {
        this.webClient = webClient;
        this.timeoutInMillis = timeoutInMillis;
    }

    public Mono<HttpStatus> getHttpStatus(String url) {
        LOGGER.info("pinging url: {}", url);
        return webClient.get()
                .uri(url)
                .exchangeToMono(clientResponse -> {
                            HttpStatus httpStatus = clientResponse.statusCode();
                            LOGGER.info("Request to {} returned with httpStatus {}", url, httpStatus);
                            return Mono.just(httpStatus);
                        }
                )
                .timeout(Duration.ofMillis(timeoutInMillis));
    }
}
