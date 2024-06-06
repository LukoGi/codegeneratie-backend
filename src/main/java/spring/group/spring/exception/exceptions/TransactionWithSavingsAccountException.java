package spring.group.spring.exception.exceptions;

public class TransactionWithSavingsAccountException extends RuntimeException {
    public TransactionWithSavingsAccountException() {
        super("Transactions to/from savings accounts are not allowed.");
    }
}
