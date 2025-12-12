package org.apiapplication.repositories;

import org.apiapplication.entities.function.FunctionMinMaxValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunctionMinMaxValueRepository extends JpaRepository<FunctionMinMaxValue, Integer> {
}
