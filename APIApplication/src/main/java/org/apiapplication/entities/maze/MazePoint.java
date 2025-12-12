package org.apiapplication.entities.maze;

import jakarta.persistence.*;
import lombok.Data;
import org.apiapplication.enums.MazePointType;

@Data
@Entity
@Table(name = "maze_points")
public class MazePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int x;
    private int y;

    @Column(name = "maze_point_type")
    private MazePointType mazePointType;

    @ManyToOne
    @JoinColumn(name = "maze_id", referencedColumnName = "id")
    private Maze maze;

    @Override
    public String toString() {
        return "x=" + x +
                ";y=" + y +
                ';';
    }
}
