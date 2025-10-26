package org.apiapplication.repositories;

import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultAssignmentRestrictionRepository extends
        JpaRepository<DefaultAssignmentRestriction, Integer> {
}
