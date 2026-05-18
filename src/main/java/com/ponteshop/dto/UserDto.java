package com.ponteshop.dto;

import com.ponteshop.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private String city;
    private String address;
    private LocalDateTime createdAt;
    private boolean isActive;
}

