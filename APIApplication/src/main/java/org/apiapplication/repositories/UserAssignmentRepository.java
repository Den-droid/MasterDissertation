package org.apiapplication.repositories;

import org.apiapplication.entities.assignment.UserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAssignmentRepository extends JpaRepository<UserAssignment, Integer> {
}
