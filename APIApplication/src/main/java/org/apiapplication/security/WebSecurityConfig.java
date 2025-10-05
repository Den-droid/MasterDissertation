package org.apiapplication.security;

import org.apiapplication.enums.UserRole;
import org.apiapplication.security.jwt.AuthEntryPointJwt;
import org.apiapplication.security.jwt.AuthTokenFilter;
import org.apiapplication.security.jwt.JwtUtils;
import org.apiapplication.security.user_details.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    private final String[] authUrl = {
            "/api/auth/signIn/password",
            "/api/auth/signIn/apiKey",
            "/api/auth/refreshToken",
            "/api/auth/signUp",
            "/api/auth/forgotPassword/tokenExists",
            "/api/auth/forgotPassword/create",
            "/api/auth/forgotPassword/change/{token}"
    };

    private final String[] studentUrls = {
            "/api/assignments/getByUserId",
            "/api/assignments/*",
            "/api/assignments/assign",
            "/api/assignments/*/startContinue",
            "/api/assignments/*/finish",
            "/api/assignments/*/giveAnswer"
    };

    private final String[] teacherUrls = {
            "/api/assignments/*/putMark"
    };

    private final String[] adminUrls = {
            "/api/permissions/givePermission",
            "/api/permissions/removePermission"
    };

    private final String[] studentTeacherUrls = {
            "/api/assignments/*/answers",
            "/api/assignments/*/marks"
    };

    private final String[] adminTeacherUrls = {
            "/api/functions",
            "/api/assignmentRestrictions/setDefaultRestriction",
            "/api/assignmentRestrictions/setRestriction",
            "/api/assignmentRestrictions/removeDefaultRestriction"
    };

    private final String[] userUrls = {
            "/api/users/*/apiKey",
            "/api/urls",
            "/api/fields"
    };

    public final String[] publicUrls = {
            "/api/subjects",
            "/api/universities",
            "/api/urls/methods",
            "/api/assignmentRestrictions/restrictionTypes"
    };

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                             AuthEntryPointJwt unauthorizedHandler,
                             JwtUtils jwtUtils) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authManagerBuilder
                .userDetailsService(userDetailsService);

        return authManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).
                csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(studentUrls).hasRole(UserRole.STUDENT.name())
                                .requestMatchers(teacherUrls).hasRole(UserRole.TEACHER.name())
                                .requestMatchers(adminUrls).hasRole(UserRole.ADMIN.name())
                                .requestMatchers(studentTeacherUrls).hasAnyRole(UserRole.STUDENT.name(),
                                        UserRole.TEACHER.name())
                                .requestMatchers(adminTeacherUrls).hasAnyRole(UserRole.ADMIN.name(),
                                        UserRole.TEACHER.name())
                                .requestMatchers(userUrls).hasAnyRole(UserRole.ADMIN.name(),
                                        UserRole.TEACHER.name(), UserRole.STUDENT.name())
                                .requestMatchers(authUrl).permitAll()
                                .requestMatchers(publicUrls).permitAll()
                                .anyRequest().authenticated()
                )
                .authenticationManager(authenticationManager(http, userDetailsService))
                .addFilterBefore(authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
