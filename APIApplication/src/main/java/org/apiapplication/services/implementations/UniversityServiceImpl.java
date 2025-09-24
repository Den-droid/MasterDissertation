package org.apiapplication.services.implementations;

import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.repositories.UniversityRepository;
import org.apiapplication.services.interfaces.UniversityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UniversityServiceImpl implements UniversityService {
    private UniversityRepository universityRepository;

    public UniversityServiceImpl(UniversityRepository universityRepository) {
        this.universityRepository = universityRepository;
    }

    @Override
    public List<UniversityDto> getAll() {
        List<UniversityDto> universityDtos = universityRepository.findAll().stream()
                .map(u -> new UniversityDto(u.getId(), u.getName()))
                .toList();

        return universityDtos;
    }
}
