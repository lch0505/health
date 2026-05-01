<template>
  <div class="page-container">
    <el-card class="points-summary-card">
      <div class="points-header">
        <div class="points-icon">
          <el-icon :size="40"><Coin /></el-icon>
        </div>
        <div class="points-info">
          <div class="points-label">当前积分</div>
          <div class="points-value">{{ pointsSummary.currentPoints || 0 }}</div>
        </div>
      </div>
      <el-row :gutter="20" class="points-stats">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ pointsSummary.totalPoints || 0 }}</div>
            <div class="stat-label">累计获得</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ pointsSummary.usedPoints || 0 }}</div>
            <div class="stat-label">已使用</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-value">{{ pointsSummary.newAchievements || 0 }}</div>
            <div class="stat-label">新勋章</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="mt-20">
      <template #header>
        <span>积分规则说明</span>
      </template>

      <div class="points-rules">
        <div
          v-for="rule in pointsConfigList"
          :key="rule.id"
          class="rule-item"
        >
          <div class="rule-header">
            <span class="rule-name">{{ rule.pointsTypeName }}</span>
            <span class="rule-points">+{{ rule.points }} 积分</span>
          </div>
          <div class="rule-desc">{{ rule.description || '暂无说明' }}</div>
        </div>
        <el-empty v-if="pointsConfigList.length === 0" description="暂无积分规则" />
      </div>
    </el-card>

    <el-card class="mt-20">
      <template #header>
        <div class="card-header">
          <span>积分记录</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </div>
      </template>

      <el-table :data="recordList" stripe v-loading="loading">
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="pointsTypeName" label="类型" width="150">
          <template #default="{ row }">
            <el-tag :type="row.points > 0 ? 'success' : 'danger'">
              {{ row.pointsTypeName || getPointsTypeText(row.pointsType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="points" label="积分" width="100">
          <template #default="{ row }">
            <span :class="row.points > 0 ? 'text-success' : 'text-danger'">
              {{ row.points > 0 ? '+' : '' }}{{ row.points }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="mt-20"
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchRecords"
        @current-change="fetchRecords"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPointsSummary, getPointsRecordList, getPointsConfigList } from '@/api/points'

const getPointsTypeText = (code) => {
  const map = {
    'daily_check_in': '每日打卡',
    'continuous_streak_7': '连续7天打卡',
    'continuous_streak_15': '连续15天打卡',
    'continuous_streak_30': '连续30天打卡',
    'goal_complete': '完成目标',
    'achievement_reward': '勋章奖励',
    'exchange': '积分兑换'
  }
  return map[code] || code
}

const loading = ref(false)
const dateRange = ref([])
const pointsSummary = reactive({
  currentPoints: 0,
  totalPoints: 0,
  usedPoints: 0,
  newAchievements: 0
})
const pointsConfigList = ref([])
const recordList = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const fetchSummary = async () => {
  try {
    const res = await getPointsSummary()
    if (res.code === 200) {
      Object.assign(pointsSummary, res.data.points)
      pointsSummary.newAchievements = res.data.newAchievements || 0
    }
  } catch (error) {
    console.error('获取积分汇总失败', error)
  }
}

const fetchPointsConfig = async () => {
  try {
    const res = await getPointsConfigList()
    if (res.code === 200) {
      pointsConfigList.value = res.data || []
    }
  } catch (error) {
    console.error('获取积分配置失败', error)
  }
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    const res = await getPointsRecordList(params)
    if (res.code === 200) {
      recordList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取积分记录失败')
  } finally {
    loading.value = false
  }
}

const handleDateChange = () => {
  pagination.page = 1
  fetchRecords()
}

onMounted(() => {
  fetchSummary()
  fetchPointsConfig()
  fetchRecords()
})
</script>

<style scoped lang="scss">
.points-summary-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;

  :deep(.el-card__body) {
    padding: 30px;
  }

  .points-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;

    .points-icon {
      width: 80px;
      height: 80px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-right: 20px;
    }

    .points-info {
      color: #fff;

      .points-label {
        font-size: 14px;
        opacity: 0.9;
      }

      .points-value {
        font-size: 48px;
        font-weight: bold;
        line-height: 1.2;
      }
    }
  }

  .points-stats {
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    padding-top: 20px;

    .stat-item {
      text-align: center;
      color: #fff;

      .stat-value {
        font-size: 24px;
        font-weight: bold;
      }

      .stat-label {
        font-size: 14px;
        opacity: 0.8;
        margin-top: 5px;
      }
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-success {
  color: #67c23a;
  font-weight: bold;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.mt-20 {
  margin-top: 20px;
}

.points-rules {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 15px;

  .rule-item {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    padding: 15px;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      border-color: #409eff;
    }

    .rule-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      .rule-name {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }

      .rule-points {
        font-size: 16px;
        font-weight: bold;
        color: #67c23a;
      }
    }

    .rule-desc {
      font-size: 13px;
      color: #909399;
      line-height: 1.5;
    }
  }
}
</style>
