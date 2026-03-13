package com.crossborder.erp.user.controller;

import com.crossborder.erp.common.result.Result;
import com.crossborder.erp.user.dto.LoginRequest;
import com.crossborder.erp.user.dto.LoginResponse;
import com.crossborder.erp.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Result.success();
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@RequestParam String refreshToken) {
        LoginResponse response = authService.refreshToken(refreshToken);
        return Result.success(response);
    }
}
