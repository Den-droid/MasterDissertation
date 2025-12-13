package org.apiapplication.dto.maze;

import org.apiapplication.dto.university.UniversityDto;

import java.util.List;

public record MazeDto(int id, int width, int height, String name, UniversityDto university,
                      List<MazePointFullDto> mazePoints) {
}
