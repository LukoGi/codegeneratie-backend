package spring.group.spring.exception.exceptions;

public class DailyTransferLimitExceededException extends RuntimeException{
    public DailyTransferLimitExceededException() {
        super("Daily transfer limit hit");
    }
}
