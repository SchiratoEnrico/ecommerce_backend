package com.betacom.ecommerce.backend.dto.inputs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
    private String email;
    private String token;
    private String newPassword;
}