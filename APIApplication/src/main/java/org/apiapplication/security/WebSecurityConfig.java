package org.apiapplication.security;

import org.apiapplication.security.jwt.AuthEntryPointJwt;
import org.apiapplication.security.jwt.AuthTokenFilter;
import org.apiapplication.security.jwt.JwtUtils;
import org.apiapplication.security.user_details.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final JwtUtils jwtUtils;

    private final String[] authUrl = {
            "/api/auth/signIn/password",
            "/api/auth/signIn/apiKey",
            "/api/auth/apiKey",
            "/api/auth/refreshToken",
            "/api/auth/signUp",
            "/api/auth/forgotPassword/tokenExists",
            "/api/auth/forgotPassword/create",
            "/api/auth/forgotPassword/change/{token}"
    };

    private final String[] assignmentUrls = {
            "/api/assignments/getByUserId",
            "/api/assignments/{assignmentId}",
            "/api/assignments/isAvailable",
            "/api/assignments/assign",
            "/api/assignments/{assignmentId}/startContinue",
            "/api/assignments/{assignmentId}/finish",
            "/api/assignments/{assignmentId}/answer",
            "/api/assignments/{assignmentId}/answers"
    };

    private final String[] markUrls = {
            "/api/assignments/{assignmentId}/mark",
            "/api/assignments/toMark"
    };

    private final String[] subjectUrls = {
            "/api/subjects",
    };

    private final String[] universityUrls = {
            "/api/universities",
    };

    private final String[] fieldUrls = {
            "/api/fields",
    };
    private final String[] urlUrls = {
            "/api/urls",
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
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).
                csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(authUrl).permitAll()
                                .requestMatchers(assignmentUrls).permitAll()
                                .requestMatchers(markUrls).permitAll()
                                .requestMatchers(urlUrls).permitAll()
                                .requestMatchers(fieldUrls).permitAll()
                                .requestMatchers(subjectUrls).permitAll()
                                .requestMatchers(universityUrls).permitAll()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
