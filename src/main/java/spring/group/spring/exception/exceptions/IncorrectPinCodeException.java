package spring.group.spring.exception.exceptions;

public class IncorrectPinCodeException extends RuntimeException{
    public IncorrectPinCodeException() {
        super("Incorrect credentials");
    }
}
