package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.dto.user.UserDto;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserInfo;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.UserInfoRepository;
import org.apiapplication.repositories.UserRepository;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;

    private final SessionService sessionService;

    public UserServiceImpl(UserInfoRepository userInfoRepository,
                           UserRepository userRepository,
                           SessionService sessionService) {
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
    }

    @Override
    public List<UserDto> get(Integer userId) {
        if (!sessionService.isUserAdmin(sessionService.getCurrentUser()))
            throw new PermissionException();

        if (userId != null) {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
            );

            return List.of(getUserDto(user));
        } else {
            List<User> users = userRepository.findAll();
            return users.stream().map(this::getUserDto).toList();
        }
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
        userInfo.setApiKey(newApiToken);

        userInfoRepository.save(userInfo);

        return new ApiKeyDto(newApiToken);
    }

    private UserDto getUserDto(User user) {
        return new UserDto(user.getId(), user.getUserInfo().getFirstName(),
                user.getUserInfo().getLastName(),
                user.getEmail(),
                user.getRoles().get(0).getName().name(),
                user.isApproved());
    }

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }
}
