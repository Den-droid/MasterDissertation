package org.apiapplication.entities.assignment;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.enums.AssignmentRestrictionType;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Data
@Table(name = "default_assignment_restrictions")
public class DefaultAssignmentRestriction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    private Function function;

    private AssignmentRestrictionType assignmentRestrictionType;
    private int attemptsRemaining;
    private int minutesForAttempt;
    private LocalDateTime deadline;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultAssignmentRestriction that)) return false;
        return Objects.equals(getUniversity(), that.getUniversity())
                && Objects.equals(getSubject(), that.getSubject())
                && Objects.equals(getFunction(), that.getFunction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniversity(), getSubject(), getFunction());
    }
}
