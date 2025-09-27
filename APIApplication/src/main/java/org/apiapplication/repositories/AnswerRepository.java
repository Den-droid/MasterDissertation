package org.apiapplication.repositories;

import org.apiapplication.entities.assignment.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
}
