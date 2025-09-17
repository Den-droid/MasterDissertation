package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.Answer;
import org.springframework.data.repository.CrudRepository;

public interface AnswerRepository extends CrudRepository<Answer, Integer> {
}
