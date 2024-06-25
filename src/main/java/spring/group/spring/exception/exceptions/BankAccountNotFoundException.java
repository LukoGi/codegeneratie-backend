package spring.group.spring.exception.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException() {
        super("Bank account not found by username");
    }
}
