package org.apiapplication.repositories;

import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAssignmentRestrictionRepository extends JpaRepository<DefaultAssignmentRestriction, Integer> {
}
