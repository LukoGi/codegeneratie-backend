package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.group.spring.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
