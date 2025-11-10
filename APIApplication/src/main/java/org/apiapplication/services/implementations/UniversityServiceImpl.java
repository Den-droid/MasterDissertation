package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.permission.PermissionDto;
import org.apiapplication.dto.university.AddUniversityDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.university.UpdateUniversityDto;
import org.apiapplication.entities.University;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserPermission;
import org.apiapplication.enums.UserRole;
import org.apiapplication.exceptions.entity.EntityCantBeDeletedException;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameAlreadyFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.UniversityRepository;
import org.apiapplication.repositories.UserPermissionRepository;
import org.apiapplication.repositories.UserRepository;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.UniversityService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UniversityServiceImpl implements UniversityService {
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;

    private final SessionService sessionService;
    private final PermissionService permissionService;

    public UniversityServiceImpl(UniversityRepository universityRepository,
                                 UserRepository userRepository,
                                 UserPermissionRepository userPermissionRepository,
                                 SessionService sessionService,
                                 PermissionService permissionService) {
        this.universityRepository = universityRepository;
        this.userRepository = userRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.sessionService = sessionService;
        this.permissionService = permissionService;
    }

    @Override
    public UniversityDto getUniversityById(int id) {
        University university = universityRepository.findById(id).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY, String.valueOf(id))
        );

        return new UniversityDto(university.getId(), university.getName());
    }

    @Override
    public List<UniversityDto> get() {
        Set<University> universities = getForUser(sessionService.getCurrentUser());

        List<UniversityDto> universityDtos = universities.stream()
                .map(u -> new UniversityDto(u.getId(), u.getName()))
                .toList();

        return universityDtos;
    }

    @Override
    public IdDto add(AddUniversityDto dto) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        Optional<University> existingUniversity = universityRepository.findAll().stream()
                .filter(u -> u.getName().equalsIgnoreCase(dto.name()))
                .findFirst();

        if (existingUniversity.isPresent()) {
            throw new EntityWithNameAlreadyFoundException(EntityName.UNIVERSITY, dto.name());
        }

        University university = new University();
        university.setName(dto.name());

        universityRepository.save(university);

        userRepository.findAll().stream()
                .filter(u -> u.getRoles().get(0).getName().equals(UserRole.ADMIN))
                .forEach(u -> permissionService.givePermission(new PermissionDto(null, u.getId(),
                        university.getId(), null, null, null)));

        return new IdDto(university.getId());
    }

    @Override
    public void update(int id, UpdateUniversityDto dto) {
        University existingUniversity = universityRepository.findById(id)
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.UNIVERSITY,
                        String.valueOf(id)));

        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        if (dto.name() != null && !dto.name().isEmpty()) {
            Optional<University> universityWithSameName = universityRepository.findAll().stream()
                    .filter(u -> u.getName().equalsIgnoreCase(dto.name()) &&
                            !u.getId().equals(id))
                    .findFirst();

            if (universityWithSameName.isPresent()) {
                throw new EntityWithNameAlreadyFoundException(EntityName.UNIVERSITY,
                        dto.name());
            }

            existingUniversity.setName(dto.name());
        }

        universityRepository.save(existingUniversity);
    }

    @Override
    public void delete(int universityId) {
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new EntityWithIdNotFoundException(
                        EntityName.UNIVERSITY, String.valueOf(universityId)
                ));

        if (!sessionService.isUserAdmin(sessionService.getCurrentUser())) {
            throw new PermissionException();
        }

        if (!university.getSubjects().isEmpty()) {
            throw new EntityCantBeDeletedException();
        }

        userPermissionRepository.deleteAll(university.getUserPermissions());
        universityRepository.delete(university);
    }

    public Set<University> getForUser(User user) {
        Set<University> universities = new HashSet<>();
        List<UserPermission> userPermissions = user.getUserPermissions();

        University university = user.getUserInfo().getUniversity();
        if (university != null) {
            universities.add(university);
        }

        for (UserPermission userPermission : userPermissions) {
            if (userPermission.getUniversity() != null) {
                universities.add(userPermission.getUniversity());
            }
        }
        return universities;
    }
}
