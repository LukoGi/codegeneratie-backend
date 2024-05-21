package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.group.spring.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t WHERE "
            + "(:startDate IS NULL OR t.start_date >= :startDate) AND "
            + "(:endDate IS NULL OR t.end_date <= :endDate) AND "
            + "(:minAmount IS NULL OR t.transfer_amount >= :minAmount) AND "
            + "(:maxAmount IS NULL OR t.transfer_amount <= :maxAmount) AND "
            + "(:iban IS NULL OR t.to_account.iban = :iban OR t.from_account.iban = :iban)")
    List<Transaction> findAllTransactionsWithFilters(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("iban") String iban);
}
