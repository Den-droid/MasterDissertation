package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Mark;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkRepository extends CrudRepository<Mark, Integer> {
}
