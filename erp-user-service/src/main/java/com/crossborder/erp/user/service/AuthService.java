package com.crossborder.erp.user.service;

import com.crossborder.erp.user.dto.LoginRequest;
import com.crossborder.erp.user.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void logout(String token);

    LoginResponse refreshToken(String refreshToken);
}
