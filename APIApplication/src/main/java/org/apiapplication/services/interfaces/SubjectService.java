package org.apiapplication.services.interfaces;

import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.subject.AddSubjectDto;
import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.dto.subject.UpdateSubjectDto;

import java.util.List;

public interface SubjectService {
    SubjectDto getSubjectById(int id);

    List<SubjectDto> get(Integer universityId);

    IdDto add(AddSubjectDto addSubjectDto);

    void update(int subjectId, UpdateSubjectDto updateSubjectDto);

    void delete(int subjectId);
}
