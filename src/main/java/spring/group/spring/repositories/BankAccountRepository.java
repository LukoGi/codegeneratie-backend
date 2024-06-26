package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {
    BankAccount findByIban(String iban);

    List<BankAccount> findByUser(User user);

    @Query("SELECT b FROM BankAccount b WHERE b.user.userId = :userId AND b.accountType = :accountType AND b.isActive = :isActive")
    Optional<BankAccount> findByUseruserIdAndAccountTypeAndIsActive(@Param("userId") Integer userId, @Param("accountType") AccountType accountType, @Param("isActive") Boolean isActive);

    @Query("SELECT b FROM BankAccount b WHERE b.user = :user AND b.accountType = :accountType")
    Optional<BankAccount> findByUserAndAccountType(@Param("user") User user, @Param("accountType") AccountType accountType);

    @Query("SELECT b.iban FROM BankAccount b WHERE b.user = :user AND b.accountType = :accountType")
    List<String> findIbansByUser(@Param("user") User user, @Param("accountType") AccountType accountType);
}
