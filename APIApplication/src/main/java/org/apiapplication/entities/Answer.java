package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "answers")
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", referencedColumnName = "id")
    private Assignment assignment;

    private String answer;
    private int answerNumber;
    private boolean isCorrect;
    private double result;
}
