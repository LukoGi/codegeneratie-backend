package spring.group.spring.exception.exceptions;

public class IncorrectPincodeException extends RuntimeException{
    public IncorrectPincodeException() {
        super("Incorrect credentials");
    }
}
