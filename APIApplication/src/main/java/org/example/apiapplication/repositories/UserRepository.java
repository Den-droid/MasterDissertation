package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>,
        PagingAndSortingRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByForgotPasswordToken(String forgotPasswordToken);

    boolean existsByForgotPasswordToken(String token);
}
