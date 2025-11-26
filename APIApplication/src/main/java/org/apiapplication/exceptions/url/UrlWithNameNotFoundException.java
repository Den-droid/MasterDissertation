package org.apiapplication.exceptions.url;

public class UrlWithNameNotFoundException extends RuntimeException {
    public UrlWithNameNotFoundException(String name, String method) {
        super(String.format("Посилання %s із методом %s не знайдено", name, method));
    }
}
