package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Function;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionRepository extends CrudRepository<Function, Integer> {
}
