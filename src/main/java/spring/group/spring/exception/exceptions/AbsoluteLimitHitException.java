package spring.group.spring.exception.exceptions;

public class AbsoluteLimitHitException extends RuntimeException{
    public AbsoluteLimitHitException() {
        super("Absolute limit hit");
    }
}
