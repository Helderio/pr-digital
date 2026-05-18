package com.ponteshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 200)
    private String name;

    @NotBlank
    @Email
    private String email;

    @Size(max = 50)
    private String phone;

    @NotBlank
    @Size(min = 6, max = 72)
    private String password;

    @Size(max = 120)
    private String city;

    @Size(max = 255)
    private String address;
}

