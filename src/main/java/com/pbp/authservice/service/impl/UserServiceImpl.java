package com.pbp.authservice.service.impl;

import com.pbp.authservice.dto.UserDto;
import com.pbp.authservice.dto.request.LoginRequest;
import com.pbp.authservice.dto.request.SignupRequest;
import com.pbp.authservice.dto.response.JwtResponse;
import com.pbp.authservice.dto.response.MessageResponse;
import com.pbp.authservice.entity.ERole;
import com.pbp.authservice.entity.Role;
import com.pbp.authservice.entity.User;
import com.pbp.authservice.repository.RoleRepo;
import com.pbp.authservice.repository.UserRepo;
import com.pbp.authservice.service.UserService;
import com.pbp.authservice.utils.JwtUtils;
import com.pbp.authservice.utils.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        UserDto userDto = UserMapper.mapToDto(userRepo.findByUsername(loginRequest.getUsername()).orElseThrow());

        if (!encoder.matches(loginRequest.getPassword(), userDto.getPassword())) {
            throw new RuntimeException("Incorrect password!");
        }

        String accessToken = jwtUtils.generateJwtToken(userDto, userDto.getRoles(), "ACCESS");
        String refreshToken = jwtUtils.generateJwtToken(userDto, userDto.getRoles(), "REFRESH");

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public MessageResponse register(SignupRequest signupRequest) {
        if (userRepo.existsByUsername(signupRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        if (userRepo.existsByEmail(signupRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepo.findByRoleName(ERole.ROLE_USER.name())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if (role.equals("admin")) {
                    Role adminRole = roleRepo.findByRoleName(ERole.ROLE_ADMIN.name())
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);
                } else {
                    Role userRole = roleRepo.findByRoleName(ERole.ROLE_USER.name())
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepo.save(user);

        return new MessageResponse("User registered successfully!");
    }
}
