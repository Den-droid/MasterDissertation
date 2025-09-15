package org.example.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.enums.AssignmentStatus;
import org.example.apiapplication.enums.FunctionResultType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments")
@Data
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_assigned_id", referencedColumnName = "id")
    private User userAssigned;

    @ManyToOne
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    private Function function;

    private AssignmentStatus status;
    private int attemptsRemaining;
    private boolean isAnswerCorrect;

    @OneToMany(mappedBy = "assignment")
    private List<Assignment> assignments = new ArrayList<>();
}
