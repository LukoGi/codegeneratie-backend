package spring.group.spring.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import spring.group.spring.models.User;

import java.awt.print.Book;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    EntityManager em;

    // constructor
    public UserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<User> findUsersByRole(String role) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> user = cq.from(User.class);
        Predicate rolePredicate = cb.equal(user.get("role"), role);
        cq.where(rolePredicate);

        TypedQuery<User> query = em.createQuery(cq);
        return query.getResultList();
    }


}
