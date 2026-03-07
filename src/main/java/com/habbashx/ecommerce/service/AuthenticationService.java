package com.habbashx.ecommerce.service;

import com.habbashx.ecommerce.dto.response.AuthenticationResponse;
import com.habbashx.ecommerce.dto.request.LoginRequest;
import com.habbashx.ecommerce.dto.request.RegisterRequest;
import com.habbashx.ecommerce.entity.Role;
import com.habbashx.ecommerce.entity.User;
import com.habbashx.ecommerce.repository.RoleRepository;
import com.habbashx.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Transactional
    public AuthenticationResponse register(@NotNull final RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("user already exists with this name: "+registerRequest.getUsername());
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("user already exists with this email: "+registerRequest.getEmail());
        }


        final Role role = roleRepository.findByRoleName("ROLE_USER").orElseThrow(
                () -> new RuntimeException("role not found")
        );

        Set<Role> roles = new HashSet<>();

        roles.add(role);

        final User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        final String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles.stream().map(Role::getRoleName).collect(Collectors.toSet()))
                .build();
    }

    public AuthenticationResponse login(@NotNull final LoginRequest loginRequest) {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);


        @Nullable final User user = (User) authentication.getPrincipal();
        assert user != null;

        final String token = jwtService.generateToken(user);


        Set<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return AuthenticationResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}
