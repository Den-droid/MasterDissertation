package org.apiapplication.entities.assignment;

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
    @JoinColumn(name = "user_assignment_id", referencedColumnName = "id")
    private UserAssignment userAssignment;

    private String answer;
    private int answerNumber;
    private boolean isCorrect;
    private boolean isWall;
    private String result;
}
