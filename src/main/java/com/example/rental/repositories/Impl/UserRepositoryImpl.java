package com.example.rental.repositories.Impl;

import com.example.rental.models.User;
import com.example.rental.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findUserByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        try {
            return entityManager.createQuery(jpql, User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public String findByEmail(String email) {
        String jpql = "SELECT u.email FROM User u WHERE u.email = :email";
        try {
            return entityManager.createQuery(jpql, String.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
