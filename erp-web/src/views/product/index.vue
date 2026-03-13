<template>
  <div class="product-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加商品</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="productList" stripe style="width: 100%">
        <el-table-column prop="internalSku" label="SKU" width="150" />
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="mainImage" label="主图" width="100">
          <template #default="{ row }">
            <el-image v-if="row.mainImage" :src="row.mainImage" style="width: 50px; height: 50px" :preview-src-list="[row.mainImage]" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const loading = ref(false)
const productList = ref<any[]>([])

const pagination = reactive({
  current: 1,
  size: 20,
  total: 0
})

const loadProducts = async () => {
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

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定删除该商品吗？', '提示', {
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
  })
}

const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.product-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
