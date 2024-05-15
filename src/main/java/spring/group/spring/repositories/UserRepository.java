package spring.group.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.group.spring.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
