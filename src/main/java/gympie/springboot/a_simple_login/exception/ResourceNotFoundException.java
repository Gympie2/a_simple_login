package gympie.springboot.a_simple_login.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with id " + id + " was not found.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
