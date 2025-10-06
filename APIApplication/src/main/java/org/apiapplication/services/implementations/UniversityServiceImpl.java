package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.NameDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.entities.University;
import org.apiapplication.exceptions.entity.EntityCantBeDeletedException;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.UniversityRepository;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.UniversityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UniversityServiceImpl implements UniversityService {
    private final UniversityRepository universityRepository;

    private final SessionService sessionService;
    private final PermissionService permissionService;

    public UniversityServiceImpl(UniversityRepository universityRepository,
                                 SessionService sessionService,
                                 PermissionService permissionService) {
        this.universityRepository = universityRepository;
        this.sessionService = sessionService;
        this.permissionService = permissionService;
    }

    @Override
    public List<UniversityDto> getAll() {
        List<UniversityDto> universityDtos = universityRepository.findAll().stream()
                .map(u -> new UniversityDto(u.getId(), u.getName()))
                .toList();

        return universityDtos;
    }

    @Override
    public void add(NameDto nameDto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        Optional<University> existingUniversity = universityRepository.findAll().stream()
                .filter(u -> u.getName().equals(nameDto.name()))
                .findFirst();

        if (existingUniversity.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.UNIVERSITY, nameDto.name());
        }

        University university = new University();
        university.setName(nameDto.name());

        universityRepository.save(university);
    }

    @Override
    public void update(UniversityDto universityDto) {
        University existingUniversity = universityRepository.findById(universityDto.id())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                        String.valueOf(universityDto.id())));

        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    existingUniversity)) {
                throw new PermissionException();
            }
        }

        existingUniversity.setName(universityDto.name());

        universityRepository.save(existingUniversity);
    }

    @Override
    public void delete(int universityId) {
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.UNIVERSITY, String.valueOf(universityId)
                ));

        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            if (!permissionService.userCanAccessUniversity(sessionService.getCurrentUser(),
                    university)) {
                throw new PermissionException();
            }
        }

        if (!university.getSubjects().isEmpty()) {
            throw new EntityCantBeDeletedException();
        }

        universityRepository.delete(university);
    }
}
