package spring.group.spring.exception.exceptions;

public class ActiveCheckingAccountNotFoundException extends RuntimeException {
    public ActiveCheckingAccountNotFoundException() {
        super("User does not have an active checking account");
    }
}