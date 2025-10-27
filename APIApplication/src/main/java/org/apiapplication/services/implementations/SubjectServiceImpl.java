package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
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
                    () -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(universityId)));

            subjects = university.getSubjects();
        } else {
            subjects = subjectRepository.findAll();
        }

        return getSubjectDtoFromSubjects(subjects);
    }

    @Override
    public IdDto add(AddSubjectDto dto) {
        Optional<Subject> existingSubject = subjectRepository.findAll().stream()
                .filter(u -> u.getName().equalsIgnoreCase(dto.name()))
                .findFirst();

        if (existingSubject.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.SUBJECT,
                    dto.name());
        }

        University university = universityRepository
                .findById(dto.universityId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                        String.valueOf(dto.universityId())));

        if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                university)) {
            throw new PermissionException();
        }

        Subject subject = new Subject();
        subject.setName(dto.name());
        subject.setUniversity(university);

        subjectRepository.save(subject);

        return new IdDto(subject.getId());
    }

    @Override
    public void update(int id, UpdateSubjectDto dto) {
        Subject existingSubject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.SUBJECT,
                        String.valueOf(id)));

        if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                existingSubject)) {
            throw new PermissionException();
        }

        if (dto.universityId() != null) {
            University university = universityRepository
                    .findById(dto.universityId())
                    .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                            String.valueOf(dto.universityId())));

            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    university)) {
                throw new PermissionException();
            }

            existingSubject.setUniversity(university);
        }

        if (dto.name() != null && !dto.name().isEmpty()) {
            Optional<Subject> subjectWithSameName = subjectRepository.findAll()
                    .stream()
                    .filter(s -> s.getName().equalsIgnoreCase(dto.name())
                            && !s.getId().equals(id))
                    .findFirst();

            if (subjectWithSameName.isPresent()) {
                throw new EntityWithNameAlreadyFoundException(EntityName.SUBJECT, dto.name());
            }

            existingSubject.setName(dto.name());
        }

        subjectRepository.save(existingSubject);
    }

    @Override
    public void delete(int subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.SUBJECT, String.valueOf(subjectId)
                ));

        if (!permissionService.userCanAccessSubject(sessionService.getCurrentUser(),
                subject)) {
            throw new PermissionException();
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
