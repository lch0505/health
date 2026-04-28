<template>
  <div class="page-container">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="flex-between">
            <div>
              <div class="stat-number">{{ dashboard.currentStreak || 0 }}</div>
              <div class="stat-label">当前连续打卡</div>
            </div>
            <div class="stat-icon primary">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="flex-between">
            <div>
              <div class="stat-number">{{ dashboard.maxStreak || 0 }}</div>
              <div class="stat-label">最长连续打卡</div>
            </div>
            <div class="stat-icon success">
              <el-icon :size="32"><Trophy /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="flex-between">
            <div>
              <div class="stat-number">{{ dashboard.todayCheckInCount || 0 }}</div>
              <div class="stat-label">今日打卡数</div>
            </div>
            <div class="stat-icon warning">
              <el-icon :size="32"><Sunny /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="flex-between">
            <div>
              <div class="stat-number">{{ dashboard.todayCompletedRecords || 0 }}</div>
              <div class="stat-label">今日完成记录</div>
            </div>
            <div class="stat-icon danger">
              <el-icon :size="32"><CircleCheckFilled /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="card-container">
          <template #header>
            <span>近7天打卡趋势</span>
          </template>
          <div ref="chartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="card-container">
          <template #header>
            <span>本周各类型完成情况</span>
          </template>
          <div ref="pieChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card class="card-container">
          <template #header>
            <span>最近打卡记录</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in recentCheckIns"
              :key="index"
              :type="item.hasCheckIn ? 'success' : 'info'"
              :timestamp="item.date"
              placement="top"
            >
              <el-card shadow="never">
                <div class="timeline-content">
                  <span v-if="item.hasCheckIn">
                    <el-tag type="success">已打卡 {{ item.checkInCount }} 次</el-tag>
                  </span>
                  <span v-else>
                    <el-tag type="info">未打卡</el-tag>
                  </span>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="card-container">
          <template #header>
            <span>各类型统计</span>
          </template>
          <el-table :data="weekStatistics" stripe>
            <el-table-column prop="typeName" label="类型" width="100" />
            <el-table-column prop="weekCount" label="本周完成次数" width="150">
              <template #default="{ row }">
                <el-progress
                  :percentage="Math.round(row.completionRate)"
                  :color="getProgressColor(row.type)"
                >
                  <span>{{ row.weekCount }}/7 天</span>
                </el-progress>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDashboard } from '@/api/statistics'

const chartRef = ref(null)
const pieChartRef = ref(null)
let lineChart = null
let pieChart = null

const dashboard = reactive({
  currentStreak: 0,
  maxStreak: 0,
  todayCheckInCount: 0,
  todayCompletedRecords: 0,
  recentCheckIns: [],
  weekStatistics: []
})

const recentCheckIns = ref([])
const weekStatistics = ref([])

const getProgressColor = (type) => {
  const colorMap = {
    wake_up: '#409eff',
    sleep: '#67c23a',
    exercise: '#e6a23c',
    water: '#409eff',
    sleep_early: '#67c23a'
  }
  return colorMap[type] || '#409eff'
}

const initLineChart = () => {
  if (!chartRef.value) return

  lineChart = echarts.init(chartRef.value)

  const dates = dashboard.recentCheckIns?.map(item => item.date) || []
  const counts = dashboard.recentCheckIns?.map(item => item.checkInCount) || []

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 4
    },
    series: [
      {
        name: '打卡次数',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#409eff',
          width: 3
        },
        itemStyle: {
          color: '#409eff'
        },
        data: counts
      }
    ]
  }

  lineChart.setOption(option)
}

const initPieChart = () => {
  if (!pieChartRef.value) return

  pieChart = echarts.init(pieChartRef.value)

  const data = dashboard.weekStatistics?.map(item => ({
    name: item.typeName,
    value: item.weekCount
  })) || []

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '完成次数',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c}次'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: data
      }
    ]
  }

  pieChart.setOption(option)
}

const fetchDashboard = async () => {
  try {
    const res = await getDashboard()
    if (res.code === 200) {
      Object.assign(dashboard, res.data)
      recentCheckIns.value = res.data.recentCheckIns || []
      weekStatistics.value = res.data.weekStatistics || []

      await nextTick()
      initLineChart()
      initPieChart()
    }
  } catch (error) {
    console.error('获取看板数据失败', error)
  }
}

const handleResize = () => {
  lineChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  fetchDashboard()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.stat-card {
  .el-card__body {
    padding: 20px;
  }
  
  .stat-number {
    font-size: 32px;
    font-weight: bold;
    color: #303133;
  }
  
  .stat-label {
    font-size: 14px;
    color: #909399;
    margin-top: 5px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &.primary {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
    }
    
    &.success {
      background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      color: #fff;
    }
    
    &.warning {
      background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      color: #fff;
    }
    
    &.danger {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: #fff;
    }
  }
}

.timeline-content {
  display: flex;
  align-items: center;
}
</style>
