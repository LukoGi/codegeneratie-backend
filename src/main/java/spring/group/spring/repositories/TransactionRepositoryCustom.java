package spring.group.spring.repositories;

import spring.group.spring.models.BankAccount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionRepositoryCustom {
    BigDecimal getSumOfTodaysTransaction(BankAccount fromAccount, LocalDateTime now);
}
