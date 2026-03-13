<template>
  <div class="finance-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>财务管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增流水</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="flowList" stripe style="width: 100%">
        <el-table-column prop="createTime" label="时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="flowType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getFlowTypeTag(row.flowType)">
              {{ getFlowTypeText(row.flowType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="关联订单" width="150" />
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">
            <span :style="{ color: getAmountColor(row.flowType) }">
              {{ formatAmount(row.amount, row.flowType) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const loading = ref(false)
const flowList = ref<any[]>([])

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const loadFlows = async () => {
  loading.value = true
  // TODO: 调用API
  loading.value = false
}

const handleAdd = () => {
  ElMessage.info('功能开发中...')
}

const getFlowTypeTag = (type: string) => {
  if (type === 'income') return 'success'
  if (type === 'refund') return 'warning'
  return 'danger'
}

const getFlowTypeText = (type: string) {
  const map: Record<string, string> = {
    income: '收入',
    expense: '支出',
    refund: '退款'
  }
  return map[type] || type
}

const getAmountColor = (type: string) => {
  if (type === 'income') return '#67c23a'
  if (type === 'refund') return '#e6a23c'
  return '#f56c6c'
}

const formatAmount = (amount: number, type: string) => {
  const prefix = type === 'income' ? '+' : (type === 'expense' ? '-' : '')
  return prefix + '¥' + amount.toFixed(2)
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  loadFlows()
})
</script>

<style scoped>
.finance-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
