package se.skoglycke.isitup.domain.service;

import se.skoglycke.isitup.util.ValueObject;

import java.time.LocalDateTime;
import java.util.Optional;

public class Service extends ValueObject {

    private final Long id;
    private final String name;
    private final String url;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private Status status;

    public Service(Long id, String name, String url, LocalDateTime createdAt, LocalDateTime updatedAt, Status status) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    public Service(Long id, String name, String url, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, name, url, createdAt, updatedAt,null);
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

    public Optional<Status> getStatus() {
        return Optional.ofNullable(status);
    }

    public Service setStatus(Status status) {
        this.status = status;
        return this;
    }
}
