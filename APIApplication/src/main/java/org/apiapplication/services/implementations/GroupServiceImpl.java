package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.group.*;
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

import java.util.HashSet;
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
    public List<GroupDto> get() {
        User currentUser = sessionService.getCurrentUser();
        if (!sessionService.isUserTeacher(currentUser)) {
            throw new PermissionException();
        }

        return currentUser.getGroups().stream()
                .map(g -> new GroupDto(
                        g.getId(),
                        g.getName(),
                        g.getStudents().stream()
                                .map(s -> new GroupStudentDto(s.getId(),
                                        s.getUserInfo().getFirstName(),
                                        s.getUserInfo().getLastName()))
                                .toList(),
                        g.getSubjects().stream()
                                .map(s -> new GroupSubjectDto(s.getId(),
                                        s.getName()))
                                .toList()
                ))
                .toList();
    }

    @Override
    public GroupDto getById(int id) {
        User currentUser = sessionService.getCurrentUser();
        if (!sessionService.isUserTeacher(currentUser))
            throw new PermissionException();

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.GROUP, String.valueOf(id)
                ));

        if (!group.getOwner().getId().equals(currentUser.getId()))
            throw new PermissionException();

        return new GroupDto(
                group.getId(),
                group.getName(),
                group.getStudents().stream()
                        .map(s -> new GroupStudentDto(s.getId(),
                                s.getUserInfo().getFirstName(),
                                s.getUserInfo().getLastName()))
                        .toList(),
                group.getSubjects().stream()
                        .map(s -> new GroupSubjectDto(s.getId(),
                                s.getName()))
                        .toList());
    }

    @Override
    public IdDto add(AddGroupDto dto) {
        if (!sessionService.isUserTeacher(sessionService.getCurrentUser()))
            throw new PermissionException();

        Optional<Group> existingGroup = groupRepository.findAll().stream()
                .filter(g -> g.getName().equalsIgnoreCase(dto.name()))
                .findFirst();

        if (existingGroup.isPresent())
            throw new EntityWithNameAlreadyFoundException(EntityName.GROUP, dto.name());

        Group group = new Group();
        group.setName(dto.name());
        group.setOwner(sessionService.getCurrentUser());

        groupRepository.save(group);

        return new IdDto(group.getId());
    }

    @Override
    public void update(int id, UpdateGroupDto dto) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(id)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        Optional<Group> existingGroup = groupRepository.findAll().stream()
                .filter(g -> g.getName().equalsIgnoreCase(dto.name()) &&
                        !g.getId().equals(id))
                .findFirst();

        if (existingGroup.isPresent())
            throw new EntityWithNameAlreadyFoundException(EntityName.GROUP, dto.name());

        if (dto.name() != null && !dto.name().isEmpty())
            group.setName(dto.name());

        groupRepository.save(group);
    }

    @Override
    public void delete(int id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(id)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        groupRepository.delete(group);
    }

    @Override
    public void setStudents(int groupId, SetStudentsDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(groupId)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        List<User> users = userRepository.findAllById(dto.userIds());
        group.setStudents(new HashSet<>(users));
        groupRepository.save(group);
    }

    @Override
    public void addStudents(int groupId, SetStudentsDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(groupId)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        List<User> users = userRepository.findAllById(dto.userIds());
        group.getStudents().addAll(users);
        groupRepository.save(group);
    }

    @Override
    public void removeStudents(int groupId, SetStudentsDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(groupId)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        List<User> users = userRepository.findAllById(dto.userIds());
        users.forEach(group.getStudents()::remove);
        groupRepository.save(group);
    }

    @Override
    public void setSubjects(int groupId, SetSubjectsDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(groupId)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        List<Subject> subjects = subjectRepository.findAllById(dto.subjectIds());
        group.setSubjects(new HashSet<>(subjects));
        groupRepository.save(group);
    }

    @Override
    public void addSubjects(int groupId, SetSubjectsDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(groupId)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        List<Subject> subjects = subjectRepository.findAllById(dto.subjectIds());
        group.getSubjects().addAll(subjects);
        groupRepository.save(group);
    }

    @Override
    public void removeSubjects(int groupId, SetSubjectsDto dto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.GROUP,
                        String.valueOf(groupId)));

        if (!Objects.equals(sessionService.getCurrentUser().getId(), group.getOwner().getId()))
            throw new PermissionException();

        List<Subject> subjects = subjectRepository.findAllById(dto.subjectIds());
        subjects.forEach(group.getSubjects()::remove);
        groupRepository.save(group);
    }
}
