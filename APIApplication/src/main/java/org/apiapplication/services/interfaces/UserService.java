package org.apiapplication.services.interfaces;

import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> get(Integer roleId);

    UserDto getById(int userId);

    ApiKeyDto getApiKey(int userId);

    void approve(int userId);

    void reject(int userId);
}
