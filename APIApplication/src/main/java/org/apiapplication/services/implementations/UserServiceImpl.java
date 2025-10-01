package org.apiapplication.services.implementations;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.entities.user.UserInfo;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.repositories.UserInfoRepository;
import org.apiapplication.services.interfaces.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserInfoRepository userInfoRepository;

    public UserServiceImpl(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public ApiKeyDto getApiKey(int userId) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, String.valueOf(userId))
        );

        String newApiToken = generateApiKey();
        userInfo.setApiKey(newApiToken);

        userInfoRepository.save(userInfo);

        return new ApiKeyDto(newApiToken);
    }

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }
}
