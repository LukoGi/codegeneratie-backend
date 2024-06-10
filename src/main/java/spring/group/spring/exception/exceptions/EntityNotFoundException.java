package spring.group.spring.exception.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException() {
        super("Entity not found in database");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
