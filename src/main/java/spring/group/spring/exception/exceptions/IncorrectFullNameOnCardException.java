package spring.group.spring.exception.exceptions;

public class IncorrectFullNameOnCardException extends RuntimeException{
    public IncorrectFullNameOnCardException() {
        super("Incorrect credentials");
    }
}
