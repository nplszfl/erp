package com.crossborder.erp.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.common.exception.BusinessException;
import com.crossborder.erp.common.result.Result;
import com.crossborder.erp.user.entity.*;
import com.crossborder.erp.user.enums.UserRole;
import com.crossborder.erp.user.enums.UserStatus;
import com.crossborder.erp.user.mapper.*;
import com.crossborder.erp.user.service.UserService;
import com.crossborder.erp.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final LoginLogMapper loginLogMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<String> login(String username, String password, String ip) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != UserStatus.ACTIVE) {
            if (user.getStatus() == UserStatus.DISABLED) {
                return Result.error("用户已被禁用");
            } else if (user.getStatus() == UserStatus.LOCKED) {
                return Result.error("用户已被锁定");
            }
            return Result.error("用户状态异常");
        }
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoles());
        
        // 记录登录日志
        recordLoginLog(user.getId(), username, ip, true, "登录成功");
        
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        log.info("用户登录成功: {}", username);
        return Result.success(token);
    }

    @Override
    public Result<String> refreshToken(String token) {
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            return Result.error("Token无效");
        }
        
        // 从Token获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        List<String> roles = jwtUtil.getRolesFromToken(token);
        
        // 生成新Token
        String newToken = jwtUtil.generateToken(userId, username, roles);
        
        log.info("Token刷新成功: {}", username);
        return Result.success(newToken);
    }

    @Override
    public Result<String> logout(String token) {
        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            return Result.error("Token无效");
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        
        // 记录登出日志
        recordLoginLog(jwtUtil.getUserIdFromToken(token), username, null, true, "登出成功");
        
        log.info("用户登出: {}", username);
        return Result.success("登出成功");
    }

    @Override
    public Page<User> listUsers(String keyword, UserRole role, UserStatus status,
                                 int page, int size) {
        Page<User> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                               .or().like(User::getRealName, keyword)
                               .or().like(User::getEmail, keyword));
        }
        
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(pageObj, wrapper);
    }

    @Override
    public User getUser(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ACTIVE);
        }
        
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        
        log.info("创建用户成功: {}", user.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        User existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不允许修改用户名
        user.setUsername(existing.getUsername());
        
        // 如果修改了密码，重新加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        log.info("更新用户成功: {}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不允许删除管理员
        if (user.getRole() == UserRole.ADMIN) {
            throw new BusinessException("不允许删除管理员");
        }
        
        // 删除用户角色关联
        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(urWrapper);
        
        // 删除用户
        userMapper.deleteById(userId);
        
        log.info("删除用户成功: {}", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 删除原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleMapper.delete(wrapper);
        
        // 分配新角色
        for (Long roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            ur.setCreateTime(LocalDateTime.now());
            userRoleMapper.insert(ur);
        }
        
        log.info("分配角色成功: 用户ID={}, 角色数={}", userId, roleIds.size());
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .map(roleMapper::selectById)
                .filter(r -> r != null)
                .map(Role::getCode)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        // 获取用户角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
        
        // 获取角色权限
        List<Long> permissionIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .flatMap(roleId -> {
                    LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
                    rpWrapper.eq(RolePermission::getRoleId, roleId);
                    return rolePermissionMapper.selectList(rpWrapper).stream();
                })
                .map(RolePermission::getPermissionId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        
        // 获取权限标识
        return permissionIds.stream()
                .map(permissionMapper::selectById)
                .filter(p -> p != null)
                .map(Permission::getCode)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains(permissionCode) || permissions.contains("*");
    }

    @Override
    public List<Role> listRoles() {
        return roleMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(Role role) {
        // 检查角色代码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, role.getCode());
        if (roleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色代码已存在");
        }
        
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);
        
        log.info("创建角色成功: {}", role.getName());
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
        log.info("更新角色成功: {}", role.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        // 检查是否有用户使用该角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleId, roleId);
        if (userRoleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("角色正在使用中，无法删除");
        }
        
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(rpWrapper);
        
        // 删除角色
        roleMapper.deleteById(roleId);
        
        log.info("删除角色成功: ID={}", roleId);
    }

    @Override
    public List<Permission> listPermissions(Long roleId) {
        if (roleId == null) {
            return permissionMapper.selectList(null);
        }
        
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        
        return rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .map(permissionMapper::selectById)
                .filter(p -> p != null)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 删除原有权限
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        // 分配新权限
        for (Long permissionId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rp.setCreateTime(LocalDateTime.now());
            rolePermissionMapper.insert(rp);
        }
        
        log.info("分配权限成功: 角色ID={}, 权限数={}", roleId, permissionIds.size());
    }

    @Override
    public Page<LoginLog> listLoginLogs(Long userId, LocalDateTime startTime,
                                          LocalDateTime endTime, int page, int size) {
        Page<LoginLog> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(LoginLog::getUserId, userId);
        }
        
        if (startTime != null) {
            wrapper.ge(LoginLog::getCreateTime, startTime);
        }
        
        if (endTime != null) {
            wrapper.le(LoginLog::getCreateTime, endTime);
        }
        
        wrapper.orderByDesc(LoginLog::getCreateTime);
        return loginLogMapper.selectPage(pageObj, wrapper);
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, String username, String ip,
                                boolean success, String message) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setIp(ip);
        log.setSuccess(success);
        log.setMessage(message);
        log.setCreateTime(LocalDateTime.now());
        loginLogMapper.insert(log);
    }
}
