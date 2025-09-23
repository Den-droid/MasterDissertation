package org.example.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.example.apiapplication.constants.EntityName;
import org.example.apiapplication.dto.auth.*;
import org.example.apiapplication.entities.user.Role;
import org.example.apiapplication.entities.user.User;
import org.example.apiapplication.entities.user.UserInfo;
import org.example.apiapplication.enums.UserRole;
import org.example.apiapplication.exceptions.auth.*;
import org.example.apiapplication.exceptions.entity.EntityWithNameNotFoundException;
import org.example.apiapplication.repositories.RoleRepository;
import org.example.apiapplication.repositories.UserInfoRepository;
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
    private final UserInfoRepository userInfoRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserInfoRepository userInfoRepository,
                           AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;

        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokensDto signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.email()).orElseThrow(
                () -> new UserWithEmailNotFoundException(signInDto.email())
        );

        if (!user.isApproved()) {
            throw new UserNotApprovedException();
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(signInDto.email(), signInDto.password()));
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
            throw new UserWithEmailExistsException(signUpDto.email());
        }

        User user = new User();
        user.setEmail(signUpDto.email());
        user.setPassword(passwordEncoder.encode(signUpDto.password()));
        user.setApproved(false);

        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(signUpDto.firstName());
        userInfo.setLastName(signUpDto.lastName());
        userInfo.setUser(user);

        List<Role> roles = new ArrayList<>();
        Role userRole = roleRepository.findByName(UserRole.valueOf(signUpDto.role()))
                .orElseThrow(() -> new EntityWithNameNotFoundException(EntityName.ROLE, signUpDto.role()));
        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);
        userInfoRepository.save(userInfo);
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