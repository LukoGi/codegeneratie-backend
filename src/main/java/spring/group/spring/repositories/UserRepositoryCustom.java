package spring.group.spring.repositories;

import spring.group.spring.models.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findUsersByRole(String role);
}
