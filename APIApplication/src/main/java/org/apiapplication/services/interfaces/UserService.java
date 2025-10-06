package org.apiapplication.services.interfaces;

import org.apiapplication.dto.auth.ApiKeyDto;
import org.apiapplication.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> get(Integer userId);

    ApiKeyDto getApiKey(int userId);
}
