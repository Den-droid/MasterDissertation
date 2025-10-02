package org.apiapplication.services.implementations;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.entities.user.UserInfo;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.permission.PermissionException;
import org.apiapplication.repositories.UserInfoRepository;
import org.apiapplication.security.utils.SessionUtil;
import org.apiapplication.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserInfoRepository userInfoRepository;

    private final SessionUtil sessionUtil;

    public UserServiceImpl(UserInfoRepository userInfoRepository,
                           SessionUtil sessionUtil) {
        this.userInfoRepository = userInfoRepository;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public ApiKeyDto getApiKey(int userId) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
        );

        if (!sessionUtil.getUserFromSession().getId().equals(userInfo.getId())) {
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
