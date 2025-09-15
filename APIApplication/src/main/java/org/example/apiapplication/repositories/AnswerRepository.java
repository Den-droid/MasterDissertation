package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Answer;
import org.example.apiapplication.entities.Assignment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnswerRepository extends CrudRepository<Answer, Integer> {
    List<Answer> findByAssignment(Assignment assignment);
}
