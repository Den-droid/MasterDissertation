package org.apiapplication.exceptions.url;

public class UrlWithNameNotFoundException extends RuntimeException{
    public UrlWithNameNotFoundException(String name){
        super(String.format("Посилання %s не знайдено", name));
    }
}
