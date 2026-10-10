package com.example.ecommerce.service;

import com.example.ecommerce.dto.ProfileUpdateRequest;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDto getProfile(String email) {
        return UserDto.from(getByEmail(email));
    }

    public UserDto updateProfile(String email, ProfileUpdateRequest req) {
        User user = getByEmail(email);
        user.name = req.name().trim();
        user.phone = req.phone().trim();
        user.address = req.address().trim();
        return UserDto.from(userRepository.save(user));
    }

    public List<UserDto> listUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(UserDto::from).toList();
    }

    public UserDto getUser(Long id) {
        return UserDto.from(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    public UserDto setEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.role == Role.ADMIN) {
            throw new BadRequestException("Administrator accounts cannot be disabled");
        }
        user.enabled = enabled;
        return UserDto.from(userRepository.save(user));
    }
}
