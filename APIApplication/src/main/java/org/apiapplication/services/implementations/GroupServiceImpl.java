package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.group.AddGroupDto;
import org.apiapplication.dto.group.SetStudentsDto;
import org.apiapplication.dto.group.SetSubjectsDto;
import org.apiapplication.dto.group.UpdateGroupDto;
import org.apiapplication.entities.Group;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.user.User;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.GroupRepository;
import org.apiapplication.repositories.SubjectRepository;
import org.apiapplication.repositories.UserRepository;
import org.apiapplication.services.interfaces.GroupService;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class GroupServiceImpl implements GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    private final SessionService sessionService;

    public GroupServiceImpl(GroupRepository groupRepository,
                            UserRepository userRepository,
                            SubjectRepository subjectRepository,
                            SessionService sessionService) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;

        this.sessionService = sessionService;
    }

    @Override
    public int add(AddGroupDto dto) {
        if (!sessionService.isUserTeacher(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        Optional<Group> existingGroup = groupRepository.findAll().stream()
                .filter(g -> g.getName().equalsIgnoreCase(dto.name()))
                .findFirst();

        if (existingGroup.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.GROUP, dto.name());
        }

        Group group = new Group();
        group.setName(dto.name());
        group.setOwner(sessionService.getCurrentUser());

        groupRepository.save(group);

        return group.getId();
    }

    @Override
    public void update(UpdateGroupDto dto) {
        Group group = groupRepository.findById(dto.id())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(dto.id())));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId())) {
            throw new PermissionException();
        }

        Optional<Group> existingGroup = groupRepository.findAll().stream()
                .filter(g -> g.getName().equalsIgnoreCase(dto.name()) &&
                        !g.getId().equals(dto.id()))
                .findFirst();

        if (existingGroup.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.GROUP, dto.name());
        }

        group.setName(dto.name());

        groupRepository.save(group);
    }

    @Override
    public void delete(int id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(id)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId())) {
            throw new PermissionException();
        }

        groupRepository.delete(group);
    }

    @Override
    public void setStudents(SetStudentsDto dto) {
        Group group = groupRepository.findById(dto.groupId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(dto.groupId())));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId())) {
            throw new PermissionException();
        }

        List<User> users = userRepository.findAllById(dto.studentIds());
        group.setStudents(users);
        groupRepository.save(group);
    }

    @Override
    public void setSubjects(SetSubjectsDto dto) {
        Group group = groupRepository.findById(dto.groupId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(dto.groupId())));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId())) {
            throw new PermissionException();
        }

        List<Subject> subjects = subjectRepository.findAllById(dto.subjectIds());
        group.setSubjects(subjects);
        groupRepository.save(group);
    }
}
