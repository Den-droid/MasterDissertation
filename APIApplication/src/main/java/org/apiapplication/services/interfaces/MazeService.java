package org.apiapplication.services.interfaces;

import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.maze.AddMazeDto;
import org.apiapplication.dto.maze.MazeDto;

import java.util.List;

public interface MazeService {
    MazeDto getById(int mazeId);

    List<MazeDto> get(Integer universityId);

    IdDto add(AddMazeDto addMazeDto);

    void delete(int id);
}
