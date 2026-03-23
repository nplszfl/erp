package com.crossborder.erp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.common.exception.BizException;
import com.crossborder.erp.common.result.Result;
import com.crossborder.erp.user.entity.User;
import com.crossborder.erp.user.mapper.UserMapper;
import com.crossborder.erp.user.service.UserService;
import com.crossborder.erp.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<String> login(String username, String password, String ip) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BizException("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BizException("用户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("用户登录成功: username={}, ip={}", username, ip);
        
        return Result.success(token);
    }

    @Override
    public void register(User user) {
        // 检查用户名是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BizException("用户名已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        log.info("用户注册成功: username={}", user.getUsername());
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Page<User> listUsers(int page, int size, String keyword) {
        Page<User> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(pageObj, wrapper);
    }

    @Override
    public void updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BizException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        log.info("密码修改成功: userId={}", userId);
    }
}