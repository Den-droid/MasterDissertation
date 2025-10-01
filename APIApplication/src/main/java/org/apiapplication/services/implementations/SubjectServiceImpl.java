package org.apiapplication.services.implementations;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.entities.Subject;
import org.apiapplication.entities.University;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.repositories.SubjectRepository;
import org.apiapplication.repositories.UniversityRepository;
import org.apiapplication.services.interfaces.SubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {
    private SubjectRepository subjectRepository;
    private UniversityRepository universityRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                              UniversityRepository universityRepository) {
        this.subjectRepository = subjectRepository;
        this.universityRepository = universityRepository;
    }

    @Override
    public List<SubjectDto> getByUniversityId(int universityId) {
        University university = universityRepository.findById(universityId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY, String.valueOf(universityId)));

        List<Subject> subjects = university.getSubjects();
        List<SubjectDto> subjectDtos = subjects.stream()
                .map(s -> new SubjectDto(s.getId(), s.getName()))
                .toList();

        return subjectDtos;
    }
}
