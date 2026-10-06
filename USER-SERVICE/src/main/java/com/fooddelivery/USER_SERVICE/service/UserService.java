package com.fooddelivery.USER_SERVICE.service;

import com.fooddelivery.USER_SERVICE.dto.RegisterRequest;
import com.fooddelivery.USER_SERVICE.entity.User;
import com.fooddelivery.USER_SERVICE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role("CUSTOMER")
                .build();

        return userRepository.save(user);
    }

    public User getUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    public User updateUser(Long id, RegisterRequest request) {

        User user = getUser(id);

        user.setName(request.name());
        user.setPhone(request.phone());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }
}