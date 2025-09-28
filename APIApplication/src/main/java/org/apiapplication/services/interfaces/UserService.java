package org.apiapplication.services.interfaces;

import org.apiapplication.dto.auth.ApiKeyDto;

public interface UserService {
    ApiKeyDto getApiKey(int userId);
}
