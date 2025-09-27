package org.apiapplication.exceptions.auth;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException() {
        super("Refresh token is either invalid or not found in database");
    }
}
