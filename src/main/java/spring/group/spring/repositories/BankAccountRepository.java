package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.group.spring.models.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    BankAccount findByIban(String iban);
}
