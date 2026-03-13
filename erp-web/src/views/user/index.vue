<template>
  <div class="user-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" :icon="Plus" fanclick="handleAdd">添加用户</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="userList" stripe style="width: 100%">
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="160">
          <template #default="{ row }">
            {{ row.lastLoginTime ? formatTime(row.lastLoginTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handleResetPassword(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const userList = ref<any[]>([])

const loadUsers = async () => {
  loading.value = true
  // TODO: 调用API
  loading.value = false
}

const handleAdd = () => {
  ElMessage.info('功能开发中...')
}

const handleEdit = (row: any) => {
  ElMessage.info('功能开发中...')
}

const handleResetPassword = (row: any) => {
  ElMessageBox.confirm(`确定重置用户 ${row.username} 的密码吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('密码已重置')
  })
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
