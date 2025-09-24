package org.apiapplication.repositories;

import org.apiapplication.entities.user.Role;
import org.apiapplication.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(UserRole name);
}
