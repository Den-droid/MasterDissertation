package org.apiapplication.utils;

import org.springframework.util.AntPathMatcher;

public class UrlMatcher {
    public static boolean areMatched(String urlRegex, String actualUrl) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(urlRegex, actualUrl);
    }
}
