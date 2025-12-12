package org.apiapplication.dto.maze;

import java.util.List;

public record AddMazeDto(String name, int width, int height, int universityId,
                         MazePointDto startPoint, MazePointDto endPoint,
                         List<MazePointDto> customWalls) {
}
