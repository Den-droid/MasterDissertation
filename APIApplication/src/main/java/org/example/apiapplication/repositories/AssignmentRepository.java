package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Assignment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
}
