package com.crossborder.erp.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.common.result.Result;
import com.crossborder.erp.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    Result<String> login(String username, String password, String ip);

    /**
     * 用户注册
     */
    void register(User user);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 分页查询用户
     */
    Page<User> listUsers(int page, int size, String keyword);

    /**
     * 更新用户
     */
    void updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);
}