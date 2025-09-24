package org.apiapplication.repositories;

import org.apiapplication.entities.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Integer> {
}
