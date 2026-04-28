<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>月度总结</span>
          <div class="month-selector">
            <el-date-picker
              v-model="selectedMonth"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
              @change="handleMonthChange"
            />
          </div>
        </div>
      </template>

      <el-row :gutter="20" class="mb-20">
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="text-center">
              <div class="summary-number">{{ summary.totalDays }}</div>
              <div class="summary-label">本月天数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="text-center">
              <div class="summary-number">{{ summary.checkInDays || 0 }}</div>
              <div class="summary-label">打卡天数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="text-center">
              <div class="summary-number">{{ summary.maxStreak || 0 }}</div>
              <div class="summary-label">最长连续打卡</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="text-center">
              <div class="summary-number">
                <el-progress
                  type="dashboard"
                  :percentage="summary.completionRate || 0"
                  :width="80"
                  :stroke-width="10"
                />
              </div>
              <div class="summary-label">完成率</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="card-container">
            <template #header>
              <span>每日打卡详情</span>
            </template>
            <div class="calendar-container">
              <div class="calendar-header">
                <span v-for="day in weekDays" :key="day" class="week-day">{{ day }}</span>
              </div>
              <div class="calendar-body">
                <div
                  v-for="day in calendarDays"
                  :key="day.date"
                  class="calendar-day"
                  :class="{
                    'other-month': day.isOtherMonth,
                    'has-check-in': day.hasCheckIn && !day.isOtherMonth,
                    'today': day.isToday
                  }"
                >
                  <div class="day-number">{{ day.day }}</div>
                  <div class="day-status" v-if="!day.isOtherMonth">
                    <el-tag
                      v-if="day.hasCheckIn"
                      type="success"
                      size="small"
                      effect="light"
                    >
                      {{ day.totalCount }}次
                    </el-tag>
                    <span v-else class="no-check-in">-</span>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="card-container">
            <template #header>
              <span>各类型统计</span>
            </template>
            <div ref="barChartRef" style="height: 300px"></div>
            <el-table :data="typeStatistics" stripe style="margin-top: 20px">
              <el-table-column prop="typeName" label="类型" width="100" />
              <el-table-column prop="count" label="完成次数" width="100" />
              <el-table-column label="完成率">
                <template #default="{ row }">
                  <el-progress
                    :percentage="Math.round((row.count / summary.totalDays) * 100)"
                    :stroke-width="10"
                  />
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { getMonthlySummary } from '@/api/statistics'

const barChartRef = ref(null)
let barChart = null

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const now = new Date()
const selectedMonth = ref(`${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`)

const summary = reactive({
  year: now.getFullYear(),
  month: now.getMonth() + 1,
  totalDays: 0,
  checkInDays: 0,
  maxStreak: 0,
  completionRate: 0
})

const calendarDays = ref([])
const typeStatistics = ref([])
const dailyDetails = ref([])

const generateCalendarDays = () => {
  const [year, month] = selectedMonth.value.split('-').map(Number)
  const firstDay = new Date(year, month - 1, 1)
  const lastDay = new Date(year, month, 0)
  const firstDayOfWeek = firstDay.getDay()
  const daysInMonth = lastDay.getDate()

  const today = new Date()
  const isCurrentMonth = today.getFullYear() === year && today.getMonth() + 1 === month

  const days = []
  const prevMonthLastDay = new Date(year, month - 1, 0).getDate()
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    days.push({
      date: `${year}-${String(month - 1).padStart(2, '0')}-${String(prevMonthLastDay - i).padStart(2, '0')}`,
      day: prevMonthLastDay - i,
      isOtherMonth: true,
      hasCheckIn: false,
      totalCount: 0
    })
  }

  for (let i = 1; i <= daysInMonth; i++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const dayDetail = dailyDetails.value.find(d => d.date === dateStr)
    
    days.push({
      date: dateStr,
      day: i,
      isOtherMonth: false,
      isToday: isCurrentMonth && today.getDate() === i,
      hasCheckIn: dayDetail ? dayDetail.totalCount > 0 : false,
      totalCount: dayDetail ? dayDetail.totalCount : 0
    })
  }

  const remainingDays = 42 - days.length
  for (let i = 1; i <= remainingDays; i++) {
    days.push({
      date: `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`,
      day: i,
      isOtherMonth: true,
      hasCheckIn: false,
      totalCount: 0
    })
  }

  calendarDays.value = days
}

const initBarChart = () => {
  if (!barChartRef.value) return

  barChart = echarts.init(barChartRef.value)

  const data = typeStatistics.value.map(item => ({
    name: item.typeName,
    value: item.count
  }))

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '完成次数',
        type: 'bar',
        barWidth: '60%',
        data: data.map(item => item.value),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#67c23a' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }

  barChart.setOption(option)
}

const fetchMonthlySummary = async () => {
  const [year, month] = selectedMonth.value.split('-').map(Number)
  
  try {
    const res = await getMonthlySummary({ year, month })
    if (res.code === 200) {
      Object.assign(summary, res.data)
      dailyDetails.value = res.data.dailyDetails || []
      typeStatistics.value = res.data.typeStatistics?.types || []

      generateCalendarDays()

      await nextTick()
      initBarChart()
    }
  } catch (error) {
    console.error('获取月度总结失败', error)
  }
}

const handleMonthChange = () => {
  fetchMonthlySummary()
}

const handleResize = () => {
  barChart?.resize()
}

onMounted(() => {
  fetchMonthlySummary()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.month-selector {
  margin-left: 10px;
}

.summary-card {
  .el-card__body {
    padding: 20px;
  }
  
  .summary-number {
    font-size: 36px;
    font-weight: bold;
    color: #409eff;
  }
  
  .summary-label {
    font-size: 14px;
    color: #909399;
    margin-top: 8px;
  }
}

.calendar-container {
  .calendar-header {
    display: flex;
    margin-bottom: 10px;
    
    .week-day {
      flex: 1;
      text-align: center;
      font-weight: bold;
      color: #606266;
      padding: 10px 0;
    }
  }
  
  .calendar-body {
    display: flex;
    flex-wrap: wrap;
    
    .calendar-day {
      width: calc(100% / 7);
      padding: 10px;
      text-align: center;
      border: 1px solid #ebeef5;
      margin: -1px 0 0 -1px;
      min-height: 70px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      
      &.other-month {
        background-color: #f5f7fa;
        color: #c0c4cc;
      }
      
      &.has-check-in {
        background-color: #f0f9eb;
      }
      
      &.today {
        border: 2px solid #409eff;
        position: relative;
        z-index: 1;
      }
      
      .day-number {
        font-size: 16px;
        font-weight: bold;
        color: #303133;
      }
      
      .day-status {
        margin-top: 5px;
        
        .no-check-in {
          color: #c0c4cc;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
