package org.apiapplication.services.implementations;

import org.apiapplication.entities.user.User;
import org.apiapplication.enums.UserRole;
import org.apiapplication.exceptions.auth.UserWithEmailNotFoundException;
import org.apiapplication.repositories.UserRepository;
import org.apiapplication.security.user_details.UserDetailsImpl;
import org.apiapplication.services.interfaces.SessionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {
    private final UserRepository userRepository;

    public SessionServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            String email = userDetails.getEmail();
            return userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new UserWithEmailNotFoundException(email));
        } else {
            return null;
        }
    }

    @Override
    public boolean isUserAdmin(User user) {
        return user.getRoles().get(0).getName().equals(UserRole.ADMIN);
    }

    @Override
    public boolean isUserTeacher(User user) {
        return user.getRoles().get(0).getName().equals(UserRole.TEACHER);
    }

    @Override
    public boolean isUserStudent(User user) {
        return user.getRoles().get(0).getName().equals(UserRole.STUDENT);
    }

    @Override
    public boolean isCurrentUser(int userId) {
        return getCurrentUser().getId().equals(userId);
    }

    @Override
    public boolean isCurrentUser(User user) {
        return getCurrentUser().getId().equals(user.getId());
    }
}
