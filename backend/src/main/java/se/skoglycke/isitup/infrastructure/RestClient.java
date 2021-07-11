package se.skoglycke.isitup.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import se.skoglycke.isitup.domain.service.Status;

@Component
public class RestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClient.class);

    private final WebClient webClient;

    public RestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Status> ping(String url) {
        LOGGER.info("pinging url: {}", url);
        return webClient.get()
                .uri(url)
                .exchangeToMono(clientResponse ->
                        clientResponse.statusCode().is2xxSuccessful()
                                ? Mono.just(Status.FAIL)
                                : Mono.just(Status.OK)
                );
    }
}
