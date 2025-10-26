package org.apiapplication.repositories;

import org.apiapplication.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByForgotPasswordToken(String forgotPasswordToken);

    boolean existsByForgotPasswordToken(String token);

    Optional<User> findByEmailIgnoreCase(String email);
}
