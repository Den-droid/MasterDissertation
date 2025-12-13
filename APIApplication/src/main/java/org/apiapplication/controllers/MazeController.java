package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.maze.AddMazeDto;
import org.apiapplication.dto.maze.MazeDto;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.services.interfaces.MazeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mazes")
@CrossOrigin
public class MazeController {
    private final MazeService mazeService;

    public MazeController(MazeService mazeService) {
        this.mazeService = mazeService;
    }

    @GetMapping
    public ResponseEntity<List<MazeDto>> getMazes(@RequestParam(required = false)
                                                  Integer universityId) {
        List<MazeDto> mazes = mazeService.get(universityId);
        return new ResponseEntity<>(mazes, HttpStatus.OK);
    }

    @GetMapping("/{mazeId}")
    public ResponseEntity<MazeDto> getMaze(@PathVariable String mazeId) {
        int mazeIdInt;
        try {
            mazeIdInt = Integer.parseInt(mazeId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.MAZE, mazeId);
        }
        MazeDto mazeDto = mazeService.getById(mazeIdInt);
        return new ResponseEntity<>(mazeDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<IdDto> addMaze(@RequestBody AddMazeDto addMazeDto) {
        IdDto idDto = mazeService.add(addMazeDto);
        return ResponseEntity.ok(idDto);
    }

    @DeleteMapping("/{mazeId}")
    public ResponseEntity<Void> deleteMaze(@PathVariable String mazeId) {
        int mazeIdInt;
        try {
            mazeIdInt = Integer.parseInt(mazeId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.MAZE, mazeId);
        }
        mazeService.delete(mazeIdInt);
        return ResponseEntity.ok().build();
    }
}
