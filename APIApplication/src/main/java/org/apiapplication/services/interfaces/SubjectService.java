package org.apiapplication.services.interfaces;

import org.apiapplication.dto.subject.SubjectDto;

import java.util.List;

public interface SubjectService {
    List<SubjectDto> get(Integer universityId);
}
