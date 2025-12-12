package org.apiapplication.repositories;

import org.apiapplication.entities.maze.Maze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MazeRepository extends JpaRepository<Maze, Integer> {
}
