package com.crossborder.erp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crossborder.erp.user.dto.LoginRequest;
import com.crossborder.erp.user.dto.LoginResponse;
import com.crossborder.erp.user.entity.User;
import com.crossborder.erp.user.mapper.UserMapper;
import com.crossborder.erp.user.service.AuthService;
import com.crossborder.erp.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements AuthService {

    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public LoginResponse login(LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = getOne(wrapper);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 生成Refresh Token
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());

        // 存储到Redis
        String redisKey = "token:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, token, jwtExpiration, TimeUnit.MILLISECONDS);

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);

        log.info("用户登录成功: {}", user.getUsername());

        return new LoginResponse(
                token,
                refreshToken,
                new LoginResponse.UserInfo(user.getId(), user.getUsername(), user.getRealNameName(), user.getEmail(), user.getAvatar())
        );
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            String redisKey = "token:" + userId;
            redisTemplate.delete(redisKey);
            log.info("用户登出: userId={}", userId);
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        Long userId = JwtUtil.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new RuntimeException("Refresh Token无效");
        }

        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String newToken = JwtUtil.generateToken(userId, user.getUsername());
        String newRefreshToken = JwtUtil.generateRefreshToken(userId);

        String redisKey = "token:" + userId;
        redisTemplate.opsForValue().set(redisKey, newToken, jwtExpiration, TimeUnit.MILLISECONDS);

        return new LoginResponse(
                newToken,
                newRefreshToken,
                new LoginResponse.UserInfo(user.getId(), user.getUsername(), user.getRealName(), user.getEmail(), user.getAvatar())
        );
    }
}
