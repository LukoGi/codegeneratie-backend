package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.group.spring.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>, TransactionRepositoryCustom {

    @Query("SELECT t FROM Transaction t WHERE "
            + "(:date IS NULL OR t.date >= :date) AND "
            + "(:minAmount IS NULL OR t.transfer_amount >= :minAmount) AND "
            + "(:maxAmount IS NULL OR t.transfer_amount <= :maxAmount) AND "
            + "(:iban IS NULL OR t.to_account.iban = :iban OR t.from_account.iban = :iban)")
    Page<Transaction> findAllTransactionsWithFilters(
            @Param("date") LocalDateTime date,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("iban") String iban,
            Pageable pageable);


    @Query("SELECT t FROM Transaction t WHERE t.initiator_user.user_id = :customerId AND "
            + "(:startDate IS NULL OR t.date >= :startDate) AND "
            + "(:endDate IS NULL OR t.date <= :endDate) AND "
            + "(:minAmount IS NULL OR t.transfer_amount >= :minAmount) AND "
            + "(:maxAmount IS NULL OR t.transfer_amount <= :maxAmount) AND "
            + "(:iban IS NULL OR t.to_account.iban = :iban OR t.from_account.iban = :iban)")
    Page<Transaction> findAllByInitiatorUserIdWithFilters(
            @Param("customerId") Integer customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("iban") String iban,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE "
            + "(:accountId IS NULL OR t.to_account.account_id = :accountId OR t.from_account.account_id = :accountId) AND "
            + "(:startDate IS NULL OR t.date >= :startDate) AND "
            + "(:endDate IS NULL OR t.date <= :endDate) AND "
            + "(:minAmount IS NULL OR t.transfer_amount >= :minAmount) AND "
            + "(:maxAmount IS NULL OR t.transfer_amount <= :maxAmount) AND "
            + "(:iban IS NULL OR t.to_account.iban = :iban OR t.from_account.iban = :iban)")
    Page<Transaction> findAllTransactionsWithAccountIdAndFilters(
            @Param("accountId") Integer accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("iban") String iban,
            Pageable pageable);
}
