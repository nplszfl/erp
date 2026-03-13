<template>
  <div class="inventory-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>库存管理</span>
          <el-button type="primary" :icon="Refresh" @click="loadInventory">刷新</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="inventoryList" stripe style="width: 100%">
        <el-table-column prop="skuCode" label="SKU" width="150" />
        <el-table-column prop="warehouseName" label="仓库" width="120" />
        <el-table-column prop="availableQty" label="可用库存" width="100" align="right" />
        <el-table-column prop="lockedQty" label="锁定库存" width="100" align="right" />
        <el-table-column prop="totalQty" label="总库存" width="100" align="right" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" align="right" />
        <el-table-column label="库存状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStockStatus(row)">
              {{ getStockStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const inventoryList = ref<any[]>([])

const loadInventory = async () => {
  loading.value = true
  // TODO: 调用API
  loading.value = false
}

const getStockStatus = (row: any) => {
  if (row.availableQty < row.safetyStock) {
    return 'danger'
  } else if (row.availableQty < row.safetyStock * 2) {
    return 'warning'
  }
  return 'success'
}

const getStockStatusText = (row: any) => {
  if (row.availableQty < row.safetyStock) {
    return '不足'
  } else if (row.availableQty < row.safetyStock * 2) {
    return '预警'
  }
  return '正常'
}

onMounted(() => {
  loadInventory()
})
</script>

<style scoped>
.inventory-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
