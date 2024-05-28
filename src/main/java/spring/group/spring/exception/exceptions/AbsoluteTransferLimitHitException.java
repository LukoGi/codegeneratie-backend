package spring.group.spring.exception.exceptions;

public class AbsoluteTransferLimitHitException extends RuntimeException{
    public AbsoluteTransferLimitHitException() {
        super("Absolute transfer limit hit");
    }
}
