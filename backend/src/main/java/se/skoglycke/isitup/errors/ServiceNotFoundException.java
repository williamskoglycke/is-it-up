package se.skoglycke.isitup.errors;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException() {
    }

    public ServiceNotFoundException(String message) {
        super(message);
    }

}
