package co.pablobastidas.extensions;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

public abstract class Failure {

    protected final String code;
    protected final String message;
    protected final Throwable exception;

    public Failure(String code, String message, Throwable throwable) {
        this.code = requireNonNull(code);
        this.message = requireNonNull(message);
        this.exception = throwable;
    }

    public Failure(String code, String message) {
        this(code, message, null);
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public Optional<Throwable> exception() {
        return Optional.ofNullable(exception);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", code, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Failure)) {
            return false;
        }
        var failure = (Failure) o;
        return Objects.equals(code, failure.code) && Objects.equals(message, failure.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }
}
