package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.maze.*;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.entities.University;
import org.apiapplication.entities.maze.Maze;
import org.apiapplication.entities.maze.MazePoint;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.MazePointType;
import org.apiapplication.exceptions.entity.EntityCantBeDeletedException;
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
    public MazeDto getById(int mazeId) {
        if (sessionService.isUserStudent(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        Maze maze = mazeRepository.findById(mazeId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.MAZE, String.valueOf(mazeId))
        );

        if (!permissionService.userCanAccessMaze(sessionService.getCurrentUser(), maze)) {
            throw new PermissionException();
        }

        return mapMazeToDto(maze);
    }

    @Override
    public List<MazeDto> get(Integer universityId) {
        User user = sessionService.getCurrentUser();
        if (sessionService.isUserStudent(user)) {
            throw new PermissionException();
        }
        List<Maze> mazes = getForUser(user);

        if (universityId != null) {
            University university = universityRepository.findById(universityId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(universityId))
            );

            if (permissionService.userCanAccessUniversity(user, university)) {
                throw new PermissionException();
            }

            mazes = mazes.stream()
                    .filter(m -> m.getUniversity().getId().equals(universityId))
                    .toList();
        }

        return mazes.stream()
                .map(this::mapMazeToDto)
                .toList();
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
        maze.setWidth(addMazeDto.width());
        maze.setHeight(addMazeDto.height());

        List<MazePoint> mazePoints = new ArrayList<>();

        for (MazePointDto wall : addMazeDto.walls()) {
            MazePoint mazePoint = new MazePoint();
            mazePoint.setX(wall.x());
            mazePoint.setY(wall.y());
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
        Maze maze = mazeRepository.findById(id).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.MAZE, String.valueOf(id))
        );

        if (!permissionService.userCanAccessMaze(sessionService.getCurrentUser(), maze)) {
            throw new PermissionException();
        }

        if (maze.getUserAssignments().isEmpty())
            throw new EntityCantBeDeletedException();

        mazeRepository.delete(maze);
    }

    private MazeDto mapMazeToDto(Maze maze) {
        UniversityDto universityDto = new UniversityDto(maze.getUniversity().getId(),
                maze.getUniversity().getName());
        List<MazePointFullDto> mazePoints = maze.getMazePoints().stream()
                .map(mp -> new MazePointFullDto(mp.getId(), mp.getX(), mp.getY(),
                        new MazePointTypeDto(mp.getMazePointType().ordinal(),
                                mp.getMazePointType().name())))
                .toList();
        return new MazeDto(maze.getId(), maze.getWidth(), maze.getHeight(),
                maze.getName(), universityDto, mazePoints);
    }

    private List<Maze> getForUser(User user) {
        List<Maze> mazeList = new ArrayList<>();
        List<UserPermission> userPermissions = user.getUserPermissions();

        for (UserPermission userPermission : userPermissions) {
            if (userPermission.getMaze() != null) {
                mazeList.add(userPermission.getMaze());
            } else if (userPermission.getUniversity() != null) {
                mazeList.addAll(userPermission.getUniversity().getMazes());
            }
        }

        return mazeList;
    }
}
