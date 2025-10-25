package org.apiapplication.services.interfaces;

import org.apiapplication.dto.university.AddUniversityDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.university.UpdateUniversityDto;

import java.util.List;

public interface UniversityService {
    List<UniversityDto> getAll();

    int add(AddUniversityDto addUniversityDto);

    void update(UpdateUniversityDto updateUniversityDto);

    void delete(int universityId);
}
