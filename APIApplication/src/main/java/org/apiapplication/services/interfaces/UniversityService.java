package org.apiapplication.services.interfaces;

import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.university.AddUniversityDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.university.UpdateUniversityDto;

import java.util.List;

public interface UniversityService {
    UniversityDto getUniversityById(int id);

    List<UniversityDto> get();

    IdDto add(AddUniversityDto addUniversityDto);

    void update(int universityId, UpdateUniversityDto updateUniversityDto);

    void delete(int universityId);
}
