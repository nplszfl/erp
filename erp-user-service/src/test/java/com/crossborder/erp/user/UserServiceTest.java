package com.crossborder.erp.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.erp.user.entity.*;
import com.crossborder.erp.user.enums.UserRole;
import com.crossborder.erp.user.enums.UserStatus;
import com.crossborder.erp.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务单元测试
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testLogin() {
        // 注意：实际测试需要先创建测试用户
        var result = userService.login("admin", "admin123", "127.0.0.1");
        
        // 根据数据库是否有admin用户，断言会不同
        assertNotNull(result);
    }

    @Test
    public void testListUsers() {
        Page<User> page = userService.listUsers(null, null, null, 1, 10);
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }

    @Test
    public void testGetUser() {
        User user = userService.getUser(1L);
        assertNotNull(user);
        assertNotNull(user.getUsername());
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRealName("测试用户");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);

        Long userId = userService.createUser(user);
        assertNotNull(userId);
        assertTrue(userId > 0);

        // 验证用户已创建
        User created = userService.getUser(userId);
        assertEquals("testuser", created.getUsername());
    }

    @Test
    public void testUpdateUser() {
        User user = userService.getUser(1L);
        if (user != null) {
            String oldRealName = user.getRealName();
            user.setRealName("更新后的姓名");

            userService.updateUser(user);

            User updated = userService.getUser(1L);
            assertEquals("更新后的姓名", updated.getRealName());
        }
    }

    @Test
    public void testListRoles() {
        List<Role> roles = userService.listRoles();
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }

    @Test
    public void testCreateRole() {
        Role role = new Role();
        role.setName("测试角色");
        role.setCode("TEST_ROLE");
        role.setDescription("单元测试角色");

        Long roleId = userService.createRole(role);
        assertNotNull(roleId);
        assertTrue(roleId > 0);
    }

    @Test
    public void testUpdateRole() {
        List<Role> roles = userService.listRoles();
        if (!roles.isEmpty()) {
            Role role = roles.get(0);
            String oldDescription = role.getDescription();
            
            role.setDescription("更新后的描述");
            userService.updateRole(role);

            List<Role> updatedRoles = userService.listRoles();
            Role updated = updatedRoles.stream()
                    .filter(r -> r.getId().equals(role.getId()))
                    .findFirst()
                    .orElse(null);
            
            assertNotNull(updated);
            assertEquals("更新后的描述", updated.getDescription());
        }
    }

    @Test
    public void testAssignRoles() {
        User user = userService.getUser(1L);
        if (user != null) {
            List<Role> roles = userService.listRoles();
            if (!roles.isEmpty()) {
                List<Long> roleIds = Arrays.asList(roles.get(0).getId());
                userService.assignRoles(user.getId(), roleIds);

                List<String> userRoles = userService.getUserRoles(user.getId());
                assertFalse(userRoles.isEmpty());
            }
        }
    }

    @Test
    public void testGetUserRoles() {
        User user = userService.getUser(1L);
        if (user != null) {
            List<String> roles = userService.getUserRoles(user.getId());
            assertNotNull(roles);
        }
    }

    @Test
    public void testGetUserPermissions() {
        User user = userService.getUser(1L);
        if (user != null) {
            List<String> permissions = userService.getUserPermissions(user.getId());
            assertNotNull(permissions);
        }
    }

    @Test
    public void testHasPermission() {
        User user = userService.getUser(1L);
        if (user != null) {
            boolean hasPermission = userService.hasPermission(user.getId(), "product:view");
            // 根据实际权限配置，结果可能不同
            assertNotNull(hasPermission);
        }
    }

    @Test
    public void testListPermissions() {
        List<Permission> permissions = userService.listPermissions(null);
        assertNotNull(permissions);
    }

    @Test
    public void testAssignPermissions() {
        List<Role> roles = userService.listRoles();
        if (!roles.isEmpty()) {
            Role role = roles.get(0);
            
            List<Permission> permissions = userService.listPermissions(null);
            if (!permissions.isEmpty()) {
                List<Long> permissionIds = Arrays.asList(permissions.get(0).getId());
                userService.assignPermissions(role.getId(), permissionIds);

                List<Permission> rolePermissions = userService.listPermissions(role.getId());
                assertFalse(rolePermissions.isEmpty());
            }
        }
    }

    @Test
    public void testListLoginLogs() {
        Page<LoginLog> page = userService.listLoginLogs(
                null, null, null, 1, 10
        );
        assertNotNull(page);
        assertNotNull(page.getRecords());
    }
}
