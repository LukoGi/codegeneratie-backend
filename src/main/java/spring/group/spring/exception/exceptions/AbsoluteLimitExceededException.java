package spring.group.spring.exception.exceptions;

public class AbsoluteLimitExceededException extends RuntimeException{
    public AbsoluteLimitExceededException() {
        super("Absolute limit hit");
    }
}
