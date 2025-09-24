package org.apiapplication.services.interfaces;

import org.apiapplication.dto.url.UrlDto;

public interface UrlService {
    UrlDto getByName(String url);
}
