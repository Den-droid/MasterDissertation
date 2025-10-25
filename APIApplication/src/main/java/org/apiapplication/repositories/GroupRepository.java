package org.apiapplication.repositories;

import org.apiapplication.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Integer> {
}
