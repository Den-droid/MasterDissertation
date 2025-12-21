package org.apiapplication.entities.assignment;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.function.Function;
import org.apiapplication.entities.maze.Maze;
import org.apiapplication.entities.user.User;
import org.apiapplication.enums.AssignmentRestrictionType;
import org.apiapplication.enums.AssignmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Table(name = "user_assignments")
public class UserAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "function_id")
    private Function function;

    @ManyToOne
    @JoinColumn(name = "maze_id")
    private Maze maze;

    @ManyToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private AssignmentStatus status;
    private AssignmentRestrictionType restrictionType;
    private boolean hasCorrectAnswer;

    private int attemptsRemaining;
    private int minutesToDo;
    private LocalDateTime deadline;

    @OneToMany(mappedBy = "userAssignment")
    private List<Answer> answers;

    @OneToMany(mappedBy = "userAssignment")
    private List<Mark> marks;

    public UserAssignment() {
    }

    public UserAssignment(User user, Function function, Assignment assignment) {
        this.user = user;
        this.function = function;
        this.assignment = assignment;
    }


    public UserAssignment(User user, Maze maze, Assignment assignment) {
        this.user = user;
        this.maze = maze;
        this.assignment = assignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAssignment that)) return false;
        return Objects.equals(user, that.user) &&
                Objects.equals(assignment, that.assignment) &&
                Objects.equals(function, that.function) &&
                Objects.equals(maze, that.maze);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, assignment, function, maze);
    }
}
