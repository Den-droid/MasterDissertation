package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.auth.*;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.exceptions.auth.TokenRefreshException;
import org.example.apiapplication.exceptions.auth.UserNotApprovedException;
import org.example.apiapplication.exceptions.auth.UserWithEmailNotFoundException;
import org.example.apiapplication.exceptions.auth.UserWithTokenNotFoundException;
import org.example.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.repositories.UserRepository;
import org.example.apiapplication.security.jwt.JwtUtils;
import org.example.apiapplication.services.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokensDto signIn(SignInDto signInDto) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(signInDto.email(), signInDto.password()));

        User user = userRepository.findByEmail(signInDto.email()).get();

        if (!user.isApproved()) {
            throw new UserNotApprovedException();
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<String> roles = user.getRoles().stream()
                .map(x -> x.getName().name())
                .toList();

        String accessToken = jwtUtils.generateAccessToken(signInDto.email(), roles, user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(signInDto.email());

        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return new TokensDto(accessToken, refreshToken);
    }

    @Override
    public TokensDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String requestRefreshToken = refreshTokenDto.refreshToken();

        if (!jwtUtils.validateRefreshToken(requestRefreshToken)) {
            throw new TokenRefreshException(requestRefreshToken, "Refresh token is invalid! " +
                    "Please sign in again to get new!");
        }

        String email = jwtUtils.getEmailFromRefreshToken(requestRefreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));

        List<String> roles = user.getRoles().stream()
                .map(x -> x.getName().name())
                .toList();

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), roles, user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new TokensDto(accessToken, refreshToken);
    }

    @Override
    public void signUp(SignUpDto signUpDto) {
        Optional<User> optionalUser = userRepository.findByEmail(signUpDto.email());
        if (optionalUser.isPresent()) {
            throw new UserWithEmailNotFoundException(signUpDto.email());
        }

        User user = new User();
        user.setEmail(signUpDto.email());
        user.setPassword(passwordEncoder.encode(signUpDto.password()));
        user.setApproved(false);

        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findById(signUpDto.roleId())
                .orElseThrow(() -> new EntityWithIdNotFoundException(EntityName.ROLE, signUpDto.roleId()));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    public void changeForgotPassword(String token, ChangePasswordDto changePasswordDto) {
        User user = userRepository
                .findByForgotPasswordToken(token)
                .orElseThrow(UserWithTokenNotFoundException::new);

        user.setForgotPasswordToken(null);
        user.setPassword(passwordEncoder.encode(changePasswordDto.newPassword()));

        userRepository.save(user);
    }

    @Override
    public void createForgotPassword(ForgotPasswordDto forgotPasswordDto) {
        User user = userRepository
                .findByEmail(forgotPasswordDto.email())
                .orElseThrow(() -> new UserWithEmailNotFoundException(forgotPasswordDto.email()));

        if (!user.isApproved()) {
            throw new UserNotApprovedException();
        }

        user.setForgotPasswordToken(UUID.randomUUID().toString());

//        emailService.forgotPassword(user.getEmail(), user.getForgotPasswordToken());

        userRepository.save(user);
    }

    @Override
    public boolean existsForgotPasswordToken(String token) {
        return userRepository.existsByForgotPasswordToken(token);
    }
}