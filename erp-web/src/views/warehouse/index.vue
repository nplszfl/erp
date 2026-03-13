<template>
  <div class="warehouse-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>仓库管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加仓库</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="warehouseList" stripe style="width: 100%">
        <el-table-column prop="warehouseCode" label="仓库编码" width="120" />
        <el-table-column prop="warehouseName" label="仓库名称" width="180" />
        <el-table-column prop="warehouseType" label="仓库类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getWarehouseType(row.warehouseType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="country" label="国家" width="100" />
        <el-table-column prop="city" label="城市" width="100" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const warehouseList = ref<any[]>([])

const loadWarehouses = async () => {
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

const getWarehouseType = (type: string) => {
  const map: Record<string, string> = {
    main: '主仓',
    overseas: '海外仓',
    third_party: '第三方仓'
  }
  return map[type] || type
}

onMounted(() => {
  loadWarehouses()
})
</script>

<style scoped>
.warehouse-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
