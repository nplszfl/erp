<template>
  <el-container class="layout-container">
    <el-aside width="220px">
      <div class="logo">跨境电商ERP</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#1890ff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        
        <el-sub-menu index="order-group">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/order">订单列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="product-group">
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/product">商品列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="customer-group">
          <template #title>
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </template>
          <el-menu-item index="/customer">客户列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="supplier-group">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>供应商管理</span>
          </template>
          <el-menu-item index="/supplier">供应商列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="inventory-group">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/inventory">库存列表</el-menu-item>
          <el-menu-item index="/inventory-alert">库存预警</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="warehouse-group">
          <template #title>
            <el-icon><House /></el-icon>
            <span>仓库管理</span>
          </template>
          <el-menu-item index="/warehouse">仓库列表</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="logistics-group">
          <template #title>
            <el-icon><Van /></el-icon>
            <span>物流管理</span>
          </template>
          <el-menu-item index="/logistics">物流中心</el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/platform">
          <el-icon><Setting /></el-icon>
          <span>平台配置</span>
        </el-menu-item>
        
        <el-menu-item index="/finance">
          <el-icon><Money /></el-icon>
          <span>财务管理</span>
        </el-menu-item>
        
        <el-menu-item index="/user">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-title">{{ pageTitle }}</div>
        <div class="header-user">
          <el-dropdown>
            <span class="el-dropdown-link">
              管理员 <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { DataAnalysis, Document, Goods, User, OfficeBuilding, Box, House, Van, Setting, Money, UserFilled, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '首页')
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #001529;
  overflow-x: hidden;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.header-title {
  font-size: 18px;
  font-weight: 500;
}

.header-user {
  cursor: pointer;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
}

.el-main {
  background-color: #f0f2f5;
  padding: 0;
}
</style>
