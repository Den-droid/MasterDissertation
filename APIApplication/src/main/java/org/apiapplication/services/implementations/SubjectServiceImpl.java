package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.subject.AddSubjectDto;
import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.dto.subject.UpdateSubjectDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.exceptions.entity.EntityCantBeDeletedException;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.SubjectRepository;
import org.apiapplication.repositories.UniversityRepository;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final UniversityRepository universityRepository;

    private final SessionService sessionService;
    private final PermissionService permissionService;

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                              UniversityRepository universityRepository,
                              SessionService sessionService,
                              PermissionService permissionService) {
        this.subjectRepository = subjectRepository;
        this.universityRepository = universityRepository;
        this.sessionService = sessionService;
        this.permissionService = permissionService;
    }

    @Override
    public List<SubjectDto> get(Integer universityId) {
        List<Subject> subjects;
        if (universityId != null) {
            University university = universityRepository.findById(universityId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY, String.valueOf(universityId)));

            subjects = university.getSubjects();
        } else {
            subjects = subjectRepository.findAll();
        }

        return getSubjectDtoFromSubjects(subjects);
    }

    @Override
    public void add(AddSubjectDto addSubjectDto) {
        Optional<Subject> existingSubject = subjectRepository.findAll().stream()
                .filter(u -> u.getName().equals(addSubjectDto.name()))
                .findFirst();

        if (existingSubject.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.SUBJECT,
                    addSubjectDto.name());
        }

        University university = universityRepository
                .findById(addSubjectDto.universityId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                        String.valueOf(addSubjectDto.universityId())));

        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    university)) {
                throw new PermissionException();
            }
        }

        Subject subject = new Subject();
        subject.setName(addSubjectDto.name());
        subject.setUniversity(university);

        subjectRepository.save(subject);
    }

    @Override
    public void update(UpdateSubjectDto updateSubjectDto) {
        Subject existingSubject = subjectRepository.findById(updateSubjectDto.id())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                        String.valueOf(updateSubjectDto.id())));

        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    existingSubject)) {
                throw new PermissionException();
            }
        }

        if (updateSubjectDto.universityId() != null) {
            University university = universityRepository
                    .findById(updateSubjectDto.universityId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(updateSubjectDto.universityId())));

            if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
                if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                        university)) {
                    throw new PermissionException();
                }
            }

            existingSubject.setUniversity(university);
        }

        Optional<Subject> subjectWithSameName = subjectRepository.findAll()
                .stream()
                .filter(s -> s.getName().equals(updateSubjectDto.name())
                        && !s.getId().equals(updateSubjectDto.id()))
                .findFirst();

        if (subjectWithSameName.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.SUBJECT, updateSubjectDto.name());
        }

        existingSubject.setName(updateSubjectDto.name());

        subjectRepository.save(existingSubject);
    }

    @Override
    public void delete(int subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.SUBJECT, String.valueOf(subjectId)
                ));

        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                    subject)) {
                throw new PermissionException();
            }
        }

        if (!subject.getFunctions().isEmpty()) {
            throw new EntityCantBeDeletedException();
        }

        subjectRepository.delete(subject);
    }

    private List<SubjectDto> getSubjectDtoFromSubjects(List<Subject> subjects) {
        return subjects.stream()
                .map(s -> new SubjectDto(s.getId(), s.getName()))
                .toList();
    }
}
