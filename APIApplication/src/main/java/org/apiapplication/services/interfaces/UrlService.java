package org.apiapplication.services.interfaces;

import org.apiapplication.dto.url.MethodTypeDto;
import org.apiapplication.dto.url.UrlDto;

import java.util.List;

public interface UrlService {
    List<UrlDto> getAllOrByUrl(String url, Integer method);

    List<MethodTypeDto> getMethods();
}
