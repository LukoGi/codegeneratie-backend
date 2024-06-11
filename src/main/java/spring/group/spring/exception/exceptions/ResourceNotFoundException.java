package spring.group.spring.exception.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Bank account not found by username");
    }
}
