package api.exceptions;

import lombok.Value;

import java.util.List;

@Value
public class ValidationException extends Exception {
    private final List<Throwable> validationExceptions;

    public ValidationException(List<Throwable> validationExceptions) {
        this.validationExceptions = validationExceptions;
    }
}
