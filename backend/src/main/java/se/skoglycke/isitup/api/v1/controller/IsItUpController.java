package se.skoglycke.isitup.api.v1.controller;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import se.skoglycke.isitup.api.v1.controller.beans.ServicesResponse;
import se.skoglycke.isitup.api.v1.controller.mapper.ServiceMapper;
import se.skoglycke.isitup.domain.IsItUpService;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static se.skoglycke.isitup.api.BaseUrl.V1;

@CrossOrigin
@RestController
@RequestMapping(V1)
public class IsItUpController {

    private final IsItUpService isItUpService;
    private final ServiceMapper serviceMapper;
    private final Cache<UUID, ServicesResponse> servicesResponseCache;
    private final Long eventInterval;

    public IsItUpController(IsItUpService isItUpService,
                            ServiceMapper serviceMapper,
                            Cache<UUID, ServicesResponse> servicesResponseCache,
                            @Value("${is-it-up.controller-event-interval}") Long eventInterval) {
        this.isItUpService = isItUpService;
        this.serviceMapper = serviceMapper;
        this.servicesResponseCache = servicesResponseCache;
        this.eventInterval = eventInterval;
    }

    @GetMapping(value = "/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServicesResponse> getStatusForServices() {
        UUID randomUUID = UUID.randomUUID(); // Need unique cache key per client/request
        Stream<ServicesResponse> servicesResponseStream = isItUpService.getAllStatuses().map(serviceMapper::mapToServicesResponse);
        return Flux
                .fromStream(servicesResponseStream)
                .delayElements(Duration.ofMillis(eventInterval))
                .flatMap(servicesResponse -> onlyEmitOnChange(servicesResponse, randomUUID));
    }

    private Flux<ServicesResponse> onlyEmitOnChange(ServicesResponse servicesResponse, UUID uuid) {

        Optional<ServicesResponse> servicesFromCache = Optional.ofNullable(servicesResponseCache.getIfPresent(uuid));

        boolean isFirstTick = servicesFromCache.isEmpty();
        if (isFirstTick) {
            servicesResponseCache.put(uuid, servicesResponse);
            return Flux.just(servicesResponse);
        }

        boolean noChangeSinceLastTick = servicesFromCache.get().equals(servicesResponse);
        if (noChangeSinceLastTick) {
            return Flux.empty();
        }

        // Put latest in cache
        servicesResponseCache.put(uuid, servicesResponse);
        return Flux.just(servicesResponse);
    }
}
