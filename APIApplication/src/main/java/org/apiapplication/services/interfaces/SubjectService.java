package org.apiapplication.services.interfaces;

import org.apiapplication.dto.subject.AddSubjectDto;
import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.dto.subject.UpdateSubjectDto;

import java.util.List;

public interface SubjectService {
    List<SubjectDto> get(Integer universityId);

    int add(AddSubjectDto addSubjectDto);

    void update(UpdateSubjectDto updateSubjectDto);

    void delete(int subjectId);
}
