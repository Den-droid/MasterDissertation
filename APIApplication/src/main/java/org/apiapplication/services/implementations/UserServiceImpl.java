package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.dto.university.UniversityDto;
import org.apiapplication.dto.user.UserDto;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserInfo;
import org.apiapplication.enums.UserRole;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.UserInfoRepository;
import org.apiapplication.repositories.UserRepository;
import org.apiapplication.services.interfaces.PermissionService;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;

    private final SessionService sessionService;
    private final PermissionService permissionService;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserInfoRepository userInfoRepository,
                           UserRepository userRepository,
                           SessionService sessionService,
                           PermissionService permissionService,
                           PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;

        this.sessionService = sessionService;
        this.permissionService = permissionService;

        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> get(Integer roleId) {
        User currentUser = sessionService.getCurrentUser();
        if (sessionService.isUserStudent(currentUser)) {
            return List.of(getUserDto(currentUser));
        }

        List<User> users;
        if (currentUser.getRoles().get(0).getName().equals(UserRole.TEACHER)) {
            users = currentUser.getUserInfo().getUniversity().getUserInfos()
                    .stream()
                    .map(UserInfo::getUser)
                    .toList();
        } else {
            users = currentUser.getUserPermissions()
                    .stream()
                    .filter(up -> up.getUniversity() != null)
                    .collect(ArrayList::new,
                            (lst, up) ->
                                    lst.addAll(up.getUniversity().getUserInfos()
                                            .stream().map(UserInfo::getUser)
                                            .toList()
                                    ), ArrayList::addAll);
        }

        if (roleId != null) {
            return users.stream()
                    .filter(u -> u.getRoles().get(0).getId().equals(roleId))
                    .map(this::getUserDto)
                    .toList();
        } else {
            return users.stream()
                    .map(this::getUserDto)
                    .toList();
        }
    }

    @Override
    public UserDto getById(int userId) {
        User currentUser = sessionService.getCurrentUser();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
        );

        if (!currentUser.getId().equals(userId) &&
                !permissionService.userCanAccessUniversity(currentUser,
                        user.getUserInfo().getUniversity())) {
            throw new PermissionException();
        }

        return getUserDto(user);
    }

    @Override
    public ApiKeyDto getApiKey(int userId) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
        );

        if (!sessionService.isCurrentUser(userInfo.getId())) {
            throw new PermissionException();
        }

        String newApiToken = generateApiKey();
        userInfo.setApiKey(passwordEncoder.encode(newApiToken));

        userInfoRepository.save(userInfo);

        return new ApiKeyDto(newApiToken);
    }

    @Override
    public void approve(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
        );

        user.setApproved(true);
        userRepository.save(user);
    }

    @Override
    public void reject(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
        );

        user.setApproved(false);
        userRepository.save(user);
    }

    private UserDto getUserDto(User user) {
        return new UserDto(user.getId(), user.getUserInfo().getFirstName(),
                user.getUserInfo().getLastName(),
                user.getEmail(),
                user.getRoles().get(0).getName().name(),
                user.isApproved(),
                new UniversityDto(user.getUserInfo().getUniversity().getId(),
                        user.getUserInfo().getUniversity().getName()));
    }

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }
}
