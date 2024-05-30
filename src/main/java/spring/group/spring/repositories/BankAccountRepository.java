package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    BankAccount findByIban(String iban);

    @Query("SELECT b FROM BankAccount b WHERE b.user.user_id = :userId AND b.account_type = :accountType AND b.is_active = :isActive")
    Optional<BankAccount> findByUserUser_idAndAccountTypeAndIsActive(@Param("userId") Integer userId, @Param("accountType") AccountType accountType, @Param("isActive") Boolean isActive);
}
