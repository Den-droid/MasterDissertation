package org.apiapplication.services.interfaces;

import org.apiapplication.dto.url.UrlDto;

import java.util.List;

public interface UrlService {
    List<UrlDto> get(String url, Integer method);
}
