package spring.group.spring.exception.exceptions;

public class IncorrectFullnameOnCardException extends RuntimeException{
    public IncorrectFullnameOnCardException() {
        super("Incorrect credentials");
    }
}
