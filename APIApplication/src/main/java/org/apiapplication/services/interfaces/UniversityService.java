package org.apiapplication.services.interfaces;

import org.apiapplication.dto.university.UniversityDto;

import java.util.List;

public interface UniversityService {
    List<UniversityDto> getAll();
}
