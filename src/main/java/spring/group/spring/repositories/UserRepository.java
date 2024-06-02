package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.group.spring.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    Optional<User> findUserByUsername(String username);

    //List<User> findByIs_approvedFalse();



    @Query("SELECT u FROM User u WHERE u.is_approved = false")
    List<User> findUnapprovedUsers();

    List<User>findByAccountsIsEmpty();




}
