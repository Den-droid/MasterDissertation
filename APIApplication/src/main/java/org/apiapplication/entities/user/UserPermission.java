package org.apiapplication.entities.user;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.UserAssignment;
import org.apiapplication.entities.function.Function;
import org.apiapplication.entities.maze.Maze;

import java.util.Objects;

@Data
@Entity
@Table(name = "user_permissions")
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "function_id", referencedColumnName = "id")
    private Function function;

    @ManyToOne
    @JoinColumn(name = "user_assignment_id", referencedColumnName = "id")
    private UserAssignment userAssignment;

    @ManyToOne
    @JoinColumn(name = "maze_id", referencedColumnName = "id")
    private Maze maze;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserPermission that)) return false;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getUniversity(), that.getUniversity()) &&
                Objects.equals(getSubject(), that.getSubject()) &&
                Objects.equals(getFunction(), that.getFunction()) &&
                Objects.equals(getUserAssignment(), that.getUserAssignment()) &&
                Objects.equals(getMaze(), that.getMaze());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getUniversity(), getSubject(), getFunction(),
                getUserAssignment(), getMaze());
    }
}
