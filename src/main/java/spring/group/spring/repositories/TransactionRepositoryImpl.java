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
        String sql = "SELECT SUM(t.transfer_amount) FROM Transaction t "+
                "WHERE t.from_account = :fromAccount " +
                "AND (t.to_account IS NULL OR t.from_account.user.id != t.to_account.user.id) " +
                "AND CAST(t.date AS date) = CAST(:now AS date)";
        Query query = em.createQuery(sql);
        query.setParameter("fromAccount", fromAccount);
        query.setParameter("now", now);
        return (BigDecimal) query.getSingleResult();
    }
}
//        String sql = "SELECT SUM(t.transfer_amount) FROM Transaction t " +
//                "JOIN BankAccount ba1 ON t.from_account.id = ba1.user.id " +
//                "JOIN BankAccount ba2 ON t.to_account.id = ba2.user.id " +
//                "WHERE t.from_account = :fromAccount " +
//                "AND CAST(t.date AS date) = CAST(:now AS date) " +
//                "AND ba1.user.id != ba2.user.id";