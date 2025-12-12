package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.maze.AddMazeDto;
import org.apiapplication.dto.maze.MazePointDto;
import org.apiapplication.entities.University;
import org.apiapplication.entities.maze.Maze;
import org.apiapplication.entities.maze.MazePoint;
import org.apiapplication.enums.MazePointType;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.MazeRepository;
import org.apiapplication.repositories.UniversityRepository;
import org.apiapplication.services.interfaces.MazePointRepository;
import org.apiapplication.services.interfaces.MazeService;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MazeServiceImpl implements MazeService {
    private final MazeRepository mazeRepository;
    private final MazePointRepository mazePointRepository;
    private final UniversityRepository universityRepository;

    private final PermissionService permissionService;
    private final SessionService sessionService;

    public MazeServiceImpl(MazeRepository mazeRepository,
                           MazePointRepository mazePointRepository,
                           UniversityRepository universityRepository,
                           PermissionService permissionService,
                           SessionService sessionService) {
        this.mazeRepository = mazeRepository;
        this.mazePointRepository = mazePointRepository;
        this.universityRepository = universityRepository;

        this.permissionService = permissionService;
        this.sessionService = sessionService;
    }

    @Override
    public IdDto add(AddMazeDto addMazeDto) {
        Optional<Maze> existingMaze = mazeRepository.findAll().stream()
                .filter(u -> u.getName().equalsIgnoreCase(addMazeDto.name()))
                .findFirst();

        if (existingMaze.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.MAZE,
                    addMazeDto.name());
        }

        University university = universityRepository
                .findById(addMazeDto.universityId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                        String.valueOf(addMazeDto.universityId())));

        if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                university)) {
            throw new PermissionException();
        }

        Maze maze = new Maze();
        maze.setUniversity(university);
        maze.setName(addMazeDto.name());

        List<MazePoint> mazePoints = new ArrayList<>();

        for (int i = 0; i < addMazeDto.width(); i++) {
            if (i != 0 && i != addMazeDto.width() - 1)
                continue;

            for (int j = 0; j < addMazeDto.height(); j++) {
                if (j != 0 && j != addMazeDto.height() - 1)
                    continue;

                MazePoint mazePoint = new MazePoint();
                mazePoint.setX(i);
                mazePoint.setY(j);
                mazePoint.setMaze(maze);
                mazePoint.setMazePointType(MazePointType.WALL);

                mazePoints.add(mazePoint);
            }
        }

        for (MazePointDto customWall : addMazeDto.customWalls()) {
            MazePoint mazePoint = new MazePoint();
            mazePoint.setX(customWall.x());
            mazePoint.setY(customWall.y());
            mazePoint.setMaze(maze);
            mazePoint.setMazePointType(MazePointType.WALL);

            mazePoints.add(mazePoint);
        }

        MazePoint startPoint = new MazePoint();
        startPoint.setX(addMazeDto.startPoint().x());
        startPoint.setY(addMazeDto.startPoint().y());
        startPoint.setMaze(maze);
        startPoint.setMazePointType(MazePointType.START);

        MazePoint endPoint = new MazePoint();
        endPoint.setX(addMazeDto.endPoint().x());
        endPoint.setY(addMazeDto.endPoint().y());
        endPoint.setMaze(maze);
        endPoint.setMazePointType(MazePointType.END);

        mazePoints.add(startPoint);
        mazePoints.add(endPoint);

        mazePointRepository.saveAll(mazePoints);
        mazeRepository.save(maze);

        return new IdDto(maze.getId());
    }

    @Override
    public void delete(int id) {

    }
}
