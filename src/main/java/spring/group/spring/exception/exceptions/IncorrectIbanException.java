package spring.group.spring.exception.exceptions;

public class IncorrectIbanException extends RuntimeException{
    public IncorrectIbanException() {
        super("Incorrect credentials");
    }
}
