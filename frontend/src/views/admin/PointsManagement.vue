<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>积分管理</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="用户积分" name="user">
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
              <el-form-item>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="userPointsList" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="totalPoints" label="累计积分" width="120">
              <template #default="{ row }">
                <span class="text-primary">{{ row.totalPoints }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="currentPoints" label="当前积分" width="120">
              <template #default="{ row }">
                <span class="text-success">{{ row.currentPoints }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="usedPoints" label="已使用" width="100">
              <template #default="{ row }">
                <span class="text-warning">{{ row.usedPoints }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="更新时间" width="200">
              <template #default="{ row }">
                {{ formatTime(row.updateTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleAdjust(row)">
                  调整积分
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination mt-20">
            <el-pagination
              v-model:current-page="searchForm.page"
              v-model:page-size="searchForm.size"
              :page-sizes="[10, 20, 50]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchUserPoints"
              @current-change="fetchUserPoints"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="积分记录" name="record">
          <div class="search-form mb-20">
            <el-form :inline="true" :model="recordSearchForm">
              <el-form-item label="用户ID">
                <el-input
                  v-model.number="recordSearchForm.userId"
                  placeholder="请输入用户ID"
                  clearable
                  @keyup.enter="handleRecordSearch"
                />
              </el-form-item>
              <el-form-item label="日期范围">
                <el-date-picker
                  v-model="recordSearchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleRecordSearch">搜索</el-button>
                <el-button @click="handleRecordReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="recordList" v-loading="recordLoading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="recordDate" label="日期" width="120" />
            <el-table-column prop="pointsTypeName" label="类型" width="150">
              <template #default="{ row }">
                <el-tag :type="row.points > 0 ? 'success' : 'danger'" size="small">
                  {{ row.pointsTypeName || row.pointsType }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明" min-width="200" />
            <el-table-column prop="points" label="积分" width="100">
              <template #default="{ row }">
                <span :class="row.points > 0 ? 'text-success' : 'text-danger'">
                  {{ row.points > 0 ? '+' : '' }}{{ row.points }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="200">
              <template #default="{ row }">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination mt-20">
            <el-pagination
              v-model:current-page="recordSearchForm.page"
              v-model:page-size="recordSearchForm.size"
              :page-sizes="[10, 20, 50]"
              :total="recordTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchRecords"
              @current-change="fetchRecords"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      title="调整积分"
      v-model="adjustDialogVisible"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form :model="adjustForm" :rules="adjustRules" ref="adjustFormRef" label-width="80px">
        <el-form-item label="用户ID">
          <el-input v-model="adjustForm.userId" disabled />
        </el-form-item>
        <el-form-item label="当前积分">
          <span class="text-success">{{ currentUserPoints }}</span>
        </el-form-item>
        <el-form-item label="调整数量" prop="points">
          <el-input-number
            v-model="adjustForm.points"
            :min="-999999"
            :max="999999"
            :step="10"
            style="width: 100%"
            placeholder="正数为增加，负数为扣除"
          />
          <div class="tip">调整后积分: {{ currentUserPoints + (adjustForm.points || 0) }}</div>
        </el-form-item>
        <el-form-item label="说明" prop="description">
          <el-input
            v-model="adjustForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入调整说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleAdjustSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getUserPointsList,
  getAdminPointsRecordList,
  adjustPoints
} from '@/api/admin'

const activeTab = ref('user')
const loading = ref(false)
const recordLoading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const recordTotal = ref(0)
const userPointsList = ref([])
const recordList = ref([])

const searchForm = reactive({
  page: 1,
  size: 10,
  userId: null
})

const recordSearchForm = reactive({
  page: 1,
  size: 10,
  userId: null,
  dateRange: []
})

const adjustDialogVisible = ref(false)
const adjustFormRef = ref(null)
const currentUserPoints = ref(0)
const adjustForm = reactive({
  userId: null,
  points: 0,
  description: ''
})

const adjustRules = {
  points: [
    { required: true, message: '请输入调整数量', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入调整说明', trigger: 'blur' }
  ]
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const fetchUserPoints = async () => {
  loading.value = true
  try {
    const params = {
      page: searchForm.page,
      size: searchForm.size
    }
    if (searchForm.userId) {
      params.userId = searchForm.userId
    }

    const res = await getUserPointsList(params)
    if (res.code === 200) {
      userPointsList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取用户积分列表失败', error)
  } finally {
    loading.value = false
  }
}

const fetchRecords = async () => {
  recordLoading.value = true
  try {
    const params = {
      page: recordSearchForm.page,
      size: recordSearchForm.size
    }
    if (recordSearchForm.userId) {
      params.userId = recordSearchForm.userId
    }
    if (recordSearchForm.dateRange && recordSearchForm.dateRange.length === 2) {
      params.startDate = recordSearchForm.dateRange[0]
      params.endDate = recordSearchForm.dateRange[1]
    }

    const res = await getAdminPointsRecordList(params)
    if (res.code === 200) {
      recordList.value = res.data.records || []
      recordTotal.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取积分记录失败', error)
  } finally {
    recordLoading.value = false
  }
}

const handleSearch = () => {
  searchForm.page = 1
  fetchUserPoints()
}

const handleReset = () => {
  searchForm.userId = null
  handleSearch()
}

const handleRecordSearch = () => {
  recordSearchForm.page = 1
  fetchRecords()
}

const handleRecordReset = () => {
  recordSearchForm.userId = null
  recordSearchForm.dateRange = []
  handleRecordSearch()
}

const handleAdjust = (row) => {
  adjustForm.userId = row.userId
  adjustForm.points = 0
  adjustForm.description = ''
  currentUserPoints.value = row.currentPoints
  adjustDialogVisible.value = true
}

const handleAdjustSubmit = async () => {
  const valid = await adjustFormRef.value.validate().catch(() => false)
  if (!valid) return

  if (currentUserPoints.value + adjustForm.points < 0) {
    ElMessage.error('积分不足，无法扣除')
    return
  }

  submitLoading.value = true
  try {
    const res = await adjustPoints({
      userId: adjustForm.userId,
      points: adjustForm.points,
      description: adjustForm.description
    })

    if (res.code === 200) {
      ElMessage.success('积分调整成功')
      adjustDialogVisible.value = false
      fetchUserPoints()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchUserPoints()
})
</script>

<style scoped lang="scss">
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.text-primary {
  color: #409eff;
  font-weight: bold;
}

.text-success {
  color: #67c23a;
  font-weight: bold;
}

.text-warning {
  color: #e6a23c;
  font-weight: bold;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
