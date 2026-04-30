<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>健康记录管理</span>
        </div>
      </template>
      
      <div class="search-form mb-20">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户ID">
            <el-input
              v-model.number="searchForm.userId"
              placeholder="请输入用户ID"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="记录类型">
            <el-select v-model="searchForm.recordType" placeholder="全部类型" clearable>
              <el-option label="运动" value="exercise" />
              <el-option label="饮水" value="water" />
              <el-option label="睡眠" value="sleep_quality" />
            </el-select>
          </el-form-item>
          <el-form-item label="开始日期">
            <el-date-picker
              v-model="searchForm.startDate"
              type="date"
              placeholder="选择开始日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker
              v-model="searchForm.endDate"
              type="date"
              placeholder="选择结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="recordDate" label="记录日期" width="120" />
        <el-table-column prop="recordType" label="记录类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ getRecordTypeName(row.recordType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量/时长" width="120">
          <template #default="{ row }">
            {{ getQuantityDisplay(row) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已完成' : '进行中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination mt-20">
        <el-pagination
          v-model:current-page="searchForm.page"
          v-model:page-size="searchForm.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getHealthRecordList } from '@/api/admin'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])

const searchForm = reactive({
  page: 1,
  size: 10,
  userId: null,
  recordType: '',
  startDate: '',
  endDate: ''
})

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const getRecordTypeName = (type) => {
  const map = {
    'exercise': '运动',
    'water': '饮水',
    'sleep_quality': '睡眠'
  }
  return map[type] || type
}

const getRecordTypeTag = (type) => {
  const map = {
    'exercise': 'warning',
    'water': 'primary',
    'sleep_quality': 'success'
  }
  return map[type] || 'info'
}

const getQuantityDisplay = (row) => {
  if (row.recordType === 'exercise') {
    return row.duration ? `${row.duration} 分钟` : '-'
  } else if (row.recordType === 'water') {
    return row.quantity ? `${row.quantity} 杯` : '-'
  } else if (row.recordType === 'sleep_quality') {
    return row.duration ? `${row.duration} 小时` : '-'
  }
  return '-'
}

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: searchForm.page,
      size: searchForm.size
    }
    if (searchForm.userId) {
      params.userId = searchForm.userId
    }
    if (searchForm.recordType) {
      params.recordType = searchForm.recordType
    }
    if (searchForm.startDate) {
      params.startDate = searchForm.startDate
    }
    if (searchForm.endDate) {
      params.endDate = searchForm.endDate
    }
    
    const res = await getHealthRecordList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取健康记录失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.userId = null
  searchForm.recordType = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  handleSearch()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.page-container {
  .card-container {
    .mb-20 {
      margin-bottom: 20px;
    }
    .mt-20 {
      margin-top: 20px;
    }
    .flex-between {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
}
</style>
