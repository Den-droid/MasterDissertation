package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.entities.user.UserInfo;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.UserInfoRepository;
import org.apiapplication.services.interfaces.SessionService;
import org.apiapplication.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserInfoRepository userInfoRepository;

    private final SessionService sessionService;

    public UserServiceImpl(UserInfoRepository userInfoRepository,
                           SessionService sessionService) {
        this.userInfoRepository = userInfoRepository;

        this.sessionService = sessionService;
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

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }
}
