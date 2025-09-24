package org.apiapplication.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.user.User;
import org.apiapplication.enums.AssignmentRestrictionType;
import org.apiapplication.enums.AssignmentStatus;

import java.time.LocalDateTime;
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
    private AssignmentRestrictionType restrictionType;
    private boolean hasCorrectAnswer;

    private int attemptsRemaining;
    private LocalDateTime lastAttemptTime;
    private int minutesForAttempt;

    @OneToMany(mappedBy = "assignment")
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "assignment")
    private List<Mark> marks = new ArrayList<>();
}
