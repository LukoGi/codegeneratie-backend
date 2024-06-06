package spring.group.spring.exception.exceptions;

public class TransactionToSavingsAccountException extends RuntimeException {
    public TransactionToSavingsAccountException() {
        super("Transactions to savings accounts are not allowed.");
    }
}
