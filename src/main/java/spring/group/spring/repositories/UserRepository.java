package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.group.spring.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
