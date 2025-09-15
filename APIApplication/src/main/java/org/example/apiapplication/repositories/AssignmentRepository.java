package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Assignment;
import org.example.apiapplication.entities.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
    List<Assignment> findByUserAssigned(User user);
}
