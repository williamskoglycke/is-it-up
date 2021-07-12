package se.skoglycke.isitup.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import se.skoglycke.isitup.domain.IsItUpService;
import se.skoglycke.isitup.domain.service.Service;
import se.skoglycke.isitup.domain.service.Status;
import se.skoglycke.isitup.infrastructure.RestClient;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

@Component
@Profile("!test")
public class UpdateServicesJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServicesJob.class);

    private final IsItUpService isItUpService;
    private final ConcurrentMap<Long, Service> serviceContext;
    private final RestClient restClient;
    private final Long refreshTime;
    private final Boolean allow3xxResponses;

    public UpdateServicesJob(IsItUpService isItUpService,
                             ConcurrentMap<Long, Service> serviceContext,
                             RestClient restClient,
                             @Value("${is-it-up.refresh-interval:5000}") Long refreshTime,
                             @Value("${is-it-up.allow-3xx-responses-as-ok:true}") Boolean allow3xxResponses) {
        this.isItUpService = isItUpService;
        this.serviceContext = serviceContext;
        this.restClient = restClient;
        this.refreshTime = refreshTime;
        this.allow3xxResponses = allow3xxResponses;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fillContextAfterStartUp() {
        isItUpService.getAllServices()
                .flatMap(service -> restClient.getHttpStatus(service.getUrl())
                        .map(this::evaluateHttpStatus)
                        .onErrorReturn(Status.FAIL)
                        .map(service::setStatus)
                        .doOnNext(this::putInContext))
                .delaySequence(Duration.ofMillis(refreshTime))
                .repeat()
                .subscribe();
    }

    private Status evaluateHttpStatus(HttpStatus httpStatus) {
        if (httpStatus.is2xxSuccessful()) {
            return Status.OK;
        }
        if (allow3xxResponses && httpStatus.is3xxRedirection()) {
            return Status.OK;
        }
        return Status.FAIL;
    }

    private void putInContext(Service serviceWithStatus) {
        LOGGER.info("Putting service {} in context", serviceWithStatus.getName());
        serviceContext.put(serviceWithStatus.getId(), serviceWithStatus);
    }

}
