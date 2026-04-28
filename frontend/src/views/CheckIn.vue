<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>今日打卡状态</span>
          <el-tag type="info">{{ currentDate }}</el-tag>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="check-in-card">
            <div class="check-in-icon" :class="{ checked: todayStatus.wakeUpChecked }">
              <el-icon :size="48" v-if="todayStatus.wakeUpChecked"><CircleCheckFilled /></el-icon>
              <el-icon :size="48" v-else><Sunny /></el-icon>
            </div>
            <div class="check-in-info">
              <h3>起床打卡</h3>
              <p v-if="todayStatus.wakeUpChecked">
                打卡时间：{{ formatTime(todayStatus.wakeUp?.checkInTime) }}
              </p>
              <p v-else class="status-text">
                <el-tag type="warning">未打卡</el-tag>
              </p>
            </div>
            <el-button
              type="primary"
              size="large"
              :loading="checkInLoading"
              :disabled="todayStatus.wakeUpChecked"
              @click="handleCheckIn('wake_up')"
            >
              {{ todayStatus.wakeUpChecked ? '已打卡' : '立即打卡' }}
            </el-button>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="check-in-card">
            <div class="check-in-icon" :class="{ checked: todayStatus.sleepChecked }">
              <el-icon :size="48" v-if="todayStatus.sleepChecked"><CircleCheckFilled /></el-icon>
              <el-icon :size="48" v-else><Moon /></el-icon>
            </div>
            <div class="check-in-info">
              <h3>睡觉打卡</h3>
              <p v-if="todayStatus.sleepChecked">
                打卡时间：{{ formatTime(todayStatus.sleep?.checkInTime) }}
              </p>
              <p v-else class="status-text">
                <el-tag type="warning">未打卡</el-tag>
              </p>
            </div>
            <el-button
              type="success"
              size="large"
              :loading="checkInLoading"
              :disabled="todayStatus.sleepChecked"
              @click="handleCheckIn('sleep')"
            >
              {{ todayStatus.sleepChecked ? '已打卡' : '立即打卡' }}
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>打卡历史记录</span>
          <el-select
            v-model="filterType"
            placeholder="全部类型"
            clearable
            style="width: 120px"
            @change="fetchCheckInList"
          >
            <el-option label="起床" value="wake_up" />
            <el-option label="睡觉" value="sleep" />
          </el-select>
        </div>
      </template>

      <el-table :data="checkInList" v-loading="listLoading" stripe>
        <el-table-column prop="checkInDate" label="日期" width="120" />
        <el-table-column prop="checkInType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.checkInType === 'wake_up' ? 'primary' : 'success'">
              {{ row.checkInType === 'wake_up' ? '起床' : '睡觉' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="打卡时间" width="200">
          <template #default="{ row }">
            {{ formatTime(row.checkInTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>

      <div class="pagination mt-20">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchCheckInList"
          @current-change="fetchCheckInList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { checkIn, getTodayStatus, getCheckInList } from '@/api/checkIn'
import { getStreakStats } from '@/api/statistics'

const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
})

const todayStatus = reactive({
  wakeUpChecked: false,
  sleepChecked: false,
  wakeUp: null,
  sleep: null
})

const checkInLoading = ref(false)
const listLoading = ref(false)
const filterType = ref('')

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const checkInList = ref([])

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const fetchTodayStatus = async () => {
  try {
    const res = await getTodayStatus()
    if (res.code === 200) {
      todayStatus.wakeUpChecked = res.data.wakeUpChecked
      todayStatus.sleepChecked = res.data.sleepChecked
      todayStatus.wakeUp = res.data.wakeUp
      todayStatus.sleep = res.data.sleep
    }
  } catch (error) {
    console.error('获取今日打卡状态失败', error)
  }
}

const fetchCheckInList = async () => {
  listLoading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (filterType.value) {
      params.checkInType = filterType.value
    }

    const res = await getCheckInList(params)
    if (res.code === 200) {
      checkInList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取打卡列表失败', error)
  } finally {
    listLoading.value = false
  }
}

const handleCheckIn = async (type) => {
  checkInLoading.value = true
  try {
    const res = await checkIn({ checkInType: type })
    if (res.code === 200) {
      ElMessage.success(type === 'wake_up' ? '起床打卡成功！' : '睡觉打卡成功！')
      fetchTodayStatus()
      fetchCheckInList()
    } else {
      ElMessage.error(res.message || '打卡失败')
    }
  } catch (error) {
    ElMessage.error('打卡失败，请稍后重试')
  } finally {
    checkInLoading.value = false
  }
}

onMounted(() => {
  fetchTodayStatus()
  fetchCheckInList()
})
</script>

<style scoped lang="scss">
.check-in-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s;
  
  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
  
  .check-in-icon {
    width: 80px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background-color: #f5f7fa;
    color: #909399;
    
    &.checked {
      background-color: #f0f9eb;
      color: #67c23a;
    }
  }
  
  .check-in-info {
    flex: 1;
    margin-left: 20px;
    
    h3 {
      margin: 0 0 10px 0;
      font-size: 18px;
      color: #303133;
    }
    
    p {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
    
    .status-text {
      margin-top: 10px;
    }
  }
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
