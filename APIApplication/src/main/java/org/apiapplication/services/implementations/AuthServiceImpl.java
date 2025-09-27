package org.apiapplication.services.implementations;

import jakarta.transaction.Transactional;
import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.auth.*;
import org.apiapplication.entities.user.Role;
import org.apiapplication.entities.user.User;
import org.apiapplication.entities.user.UserInfo;
import org.apiapplication.enums.UserRole;
import org.apiapplication.exceptions.auth.*;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.exceptions.entity.EntityWithNameNotFoundException;
import org.apiapplication.repositories.RoleRepository;
import org.apiapplication.repositories.UserInfoRepository;
import org.apiapplication.repositories.UserRepository;
import org.apiapplication.security.jwt.JwtUtils;
import org.apiapplication.services.interfaces.AuthService;
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

    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserInfoRepository userInfoRepository,
                           JwtUtils jwtUtils,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userInfoRepository = userInfoRepository;

        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TokensDto signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.email()).orElseThrow(
                UserWithEmailOrPasswordNotFoundException::new
        );

        if (!passwordEncoder.matches(signInDto.password(), user.getPassword())) {
            throw new UserWithEmailOrPasswordNotFoundException();
        }

        if (!user.isApproved()) {
            throw new UserNotApprovedException();
        }

        return getTokensDto(user);
    }

    @Override
    public TokensDto signIn(ApiKeyDto apiKeyDto) {
        UserInfo userInfo = userInfoRepository.findByApiKey(apiKeyDto.apiKey()).orElseThrow(
                () -> new UserWithKeyNotFoundException(apiKeyDto.apiKey())
        );

        return getTokensDto(userInfo.getUser());
    }

    @Override
    public TokensDto refreshToken(RefreshTokenDto refreshTokenDto) {
        String requestRefreshToken = refreshTokenDto.refreshToken();

        if (!jwtUtils.validateRefreshToken(requestRefreshToken)) {
            throw new TokenRefreshException();
        }

        String email = jwtUtils.getEmailFromRefreshToken(requestRefreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(
                TokenRefreshException::new);

        return getTokensDto(user);
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
        userInfo.setApiKey(generateApiKey());
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
        Optional<User> user = userRepository
                .findByEmail(forgotPasswordDto.email());

        if (user.isPresent()) {
            user.get().setForgotPasswordToken(UUID.randomUUID().toString());
            userRepository.save(user.get());
        }
    }

    @Override
    public boolean existsForgotPasswordToken(String token) {
        return userRepository.existsByForgotPasswordToken(token);
    }

    @Override
    public ApiKeyDto getApiKey(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityWithIdNotFoundException(EntityName.USER, userId)
        );

        String newApiToken = generateApiKey();
        user.getUserInfo().setApiKey(newApiToken);

        userInfoRepository.save(user.getUserInfo());

        return new ApiKeyDto(newApiToken);
    }

    private TokensDto getTokensDto(User user) {
        List<String> roles = user.getRoles().stream()
                .map(x -> x.getName().name())
                .toList();

        String accessToken = jwtUtils.generateAccessToken(user.getEmail(), roles, user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);

        userRepository.save(user);

        return new TokensDto(accessToken, refreshToken);
    }

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }
}