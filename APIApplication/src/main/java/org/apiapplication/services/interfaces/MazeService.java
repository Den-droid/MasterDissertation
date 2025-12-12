package org.apiapplication.services.interfaces;

import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.maze.AddMazeDto;

public interface MazeService {
    IdDto add(AddMazeDto addMazeDto);

    void delete(int id);
}
