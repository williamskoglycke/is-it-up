package se.skoglycke.isitup.errors;

public class AlreadyInDatabaseException extends RuntimeException {

    public AlreadyInDatabaseException() {
    }

    public AlreadyInDatabaseException(String message) {
        super(message);
    }

}
