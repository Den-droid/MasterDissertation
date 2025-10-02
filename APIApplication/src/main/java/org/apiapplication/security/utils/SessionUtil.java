package org.apiapplication.security.utils;

import org.apiapplication.entities.user.User;
import org.apiapplication.exceptions.auth.UserWithEmailNotFoundException;
import org.apiapplication.repositories.UserRepository;
import org.apiapplication.security.user_details.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {
    private final UserRepository userRepository;

    public SessionUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserFromSession() {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserWithEmailNotFoundException(email));
    }

}
