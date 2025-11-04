package com.gameacademy.dto;

import com.gameacademy.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String userId;
    private User.UserRole role;
    private String username;
    private String email;
}
