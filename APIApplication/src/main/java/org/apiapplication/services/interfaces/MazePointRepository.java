package org.apiapplication.services.interfaces;

import org.apiapplication.entities.maze.MazePoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MazePointRepository extends JpaRepository<MazePoint, Integer> {
}
