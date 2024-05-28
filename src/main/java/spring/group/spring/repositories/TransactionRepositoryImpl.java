package spring.group.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import spring.group.spring.models.BankAccount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    private final EntityManager em;

    public TransactionRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public BigDecimal getSumOfTodaysTransaction(BankAccount fromAccount, LocalDateTime now) {
        String sql = "SELECT SUM(t.transfer_amount) FROM Transaction t WHERE t.from_account = :fromAccount AND DATE(t.start_date) = DATE(:now)";
        Query query = em.createQuery(sql);
        query.setParameter("fromAccount", fromAccount);
        query.setParameter("now", now);
        return (BigDecimal) query.getSingleResult();
    }
}
