package spring.group.spring.exception.exceptions;

public class DailyTransferLimitHitException extends RuntimeException{
    public DailyTransferLimitHitException() {
        super("Daily transfer limit hit");
    }
}
