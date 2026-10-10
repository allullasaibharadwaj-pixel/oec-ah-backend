package com.example.ecommerce.dto;

import com.example.ecommerce.entity.User;
import java.time.LocalDateTime;

public record UserDto(Long id, String name, String email, String phone, String address,
                      String role, boolean enabled, LocalDateTime createdAt) {
    public static UserDto from(User u) {
        return new UserDto(u.id, u.name, u.email, u.phone, u.address, u.role.name(), u.enabled, u.createdAt);
    }
}
