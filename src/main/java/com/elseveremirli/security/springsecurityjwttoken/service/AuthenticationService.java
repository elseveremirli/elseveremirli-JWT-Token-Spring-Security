package com.elseveremirli.security.springsecurityjwttoken.service;

import com.elseveremirli.security.springsecurityjwttoken.dto.UserDto;
import com.elseveremirli.security.springsecurityjwttoken.dto.UserRequest;
import com.elseveremirli.security.springsecurityjwttoken.dto.UserResponse;
import com.elseveremirli.security.springsecurityjwttoken.entity.User;
import com.elseveremirli.security.springsecurityjwttoken.enums.Role;
import com.elseveremirli.security.springsecurityjwttoken.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    public UserResponse save(UserDto userDto) {
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nameSurname(userDto.getNameSurname())
                .authorities(Collections.singleton(Role.ROLE_USER))
                .accountNonExpired(true)
                .isEnabled(true)
                .accountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();
        userRepository.save(user);
        var token = jwtService.generateToken(user);
        return UserResponse.builder().token(token).build();

    }

    public UserResponse auth(UserRequest userRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
        User user = userRepository.findByUsername(userRequest.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return UserResponse.builder().token(token).build();
    }
}