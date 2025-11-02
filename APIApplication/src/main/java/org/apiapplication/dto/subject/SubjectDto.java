package org.apiapplication.dto.subject;

import org.apiapplication.dto.university.UniversityDto;

public record SubjectDto(int id, String name, UniversityDto university) {
}
