package org.apiapplication.services.interfaces;

import org.apiapplication.dto.common.NameDto;
import org.apiapplication.dto.university.UniversityDto;

import java.util.List;

public interface UniversityService {
    List<UniversityDto> getAll();

    void add(NameDto nameDto);

    void update(UniversityDto universityDto);

    void delete(int universityId);
}
