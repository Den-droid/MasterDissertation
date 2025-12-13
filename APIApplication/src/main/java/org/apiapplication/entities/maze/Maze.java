package org.apiapplication.entities.maze;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.entities.University;
import org.apiapplication.entities.assignment.DefaultAssignmentRestriction;
import org.apiapplication.entities.assignment.UserAssignment;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "mazes")
@Data
public class Maze {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private int width;
    private int height;

    @ManyToOne
    @JoinColumn(name = "university_id", referencedColumnName = "id")
    private University university;

    @OneToMany(mappedBy = "maze")
    private List<UserAssignment> userAssignments;

    @OneToMany(mappedBy = "maze")
    private List<MazePoint> mazePoints;

    @OneToMany(mappedBy = "maze")
    private List<DefaultAssignmentRestriction> defaultAssignmentRestrictions;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Maze maze)) return false;
        return Objects.equals(getId(), maze.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
