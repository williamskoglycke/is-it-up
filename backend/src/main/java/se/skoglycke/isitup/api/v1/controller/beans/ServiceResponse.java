package se.skoglycke.isitup.api.v1.controller.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import se.skoglycke.isitup.util.ValueObject;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse extends ValueObject {

    private final StatusResponse status;
    private final Long id;
    private final String name;
    private final String url;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ServiceResponse(StatusResponse status, Long id, String name, String url, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.status = status;
        this.id = id;
        this.name = name;
        this.url = url;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public StatusResponse getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
