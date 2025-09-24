package org.apiapplication.dto.url;

import org.apiapplication.enums.MethodType;

public record UrlDto(int id, String url, String description, MethodType method) {

}
