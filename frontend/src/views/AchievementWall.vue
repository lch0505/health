<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>勋章墙</span>
          <div class="stats">
            <el-tag type="primary">已获得: {{ obtainedCount }}/{{ totalCount }}</el-tag>
            <el-tag type="warning" v-if="newCount > 0" class="ml-10">
              新获得: {{ newCount }}
            </el-tag>
          </div>
        </div>
      </template>

      <div class="achievement-grid">
        <div
          v-for="achievement in achievementList"
          :key="achievement.id"
          class="achievement-item"
          :class="{ obtained: achievement.obtained, 'is-new': achievement.isNew }"
          @click="handleAchievementClick(achievement)"
        >
          <div class="achievement-icon">
            <el-icon v-if="achievement.obtained" :size="48"><Trophy /></el-icon>
            <el-icon v-else :size="48" class="locked"><Lock /></el-icon>
          </div>
          <div class="achievement-info">
            <div class="achievement-name">{{ achievement.name }}</div>
            <div class="achievement-desc">{{ achievement.requirementDescription }}</div>
            <div class="achievement-reward">
              <el-tag v-if="achievement.obtained" type="success" size="small">
                +{{ achievement.pointsReward }}积分
              </el-tag>
              <el-tag v-else type="info" size="small">
                奖励: {{ achievement.pointsReward }}积分
              </el-tag>
            </div>
          </div>
          <div class="achievement-badge" v-if="achievement.isNew">
            <el-badge is-dot />
          </div>
        </div>
      </div>

      <el-empty v-if="achievementList.length === 0" description="暂无勋章数据" />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="currentAchievement?.name"
      width="500px"
    >
      <div class="achievement-detail">
        <div class="detail-icon">
          <el-icon v-if="currentAchievement?.obtained" :size="80" class="text-success"><Trophy /></el-icon>
          <el-icon v-else :size="80" class="text-gray"><Lock /></el-icon>
        </div>
        <div class="detail-info">
          <div class="detail-name">{{ currentAchievement?.name }}</div>
          <div class="detail-desc">{{ currentAchievement?.description }}</div>
          <div class="detail-req">
            <span class="req-label">获得条件:</span>
            <span>{{ currentAchievement?.requirementDescription }}</span>
          </div>
          <div class="detail-reward">
            <span class="req-label">奖励积分:</span>
            <span class="text-success">+{{ currentAchievement?.pointsReward }}</span>
          </div>
          <div class="detail-date" v-if="currentAchievement?.obtained">
            <span class="req-label">获得时间:</span>
            <span>{{ currentAchievement?.obtainDate }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAchievementWall, markAchievementAsRead } from '@/api/achievement'

const loading = ref(false)
const achievementList = ref([])
const totalCount = ref(0)
const obtainedCount = ref(0)
const newCount = ref(0)
const dialogVisible = ref(false)
const currentAchievement = ref(null)

const fetchAchievements = async () => {
  loading.value = true
  try {
    const res = await getAchievementWall()
    if (res.code === 200) {
      achievementList.value = res.data.achievements || []
      totalCount.value = res.data.totalCount || 0
      obtainedCount.value = res.data.obtainedCount || 0
      newCount.value = res.data.newCount || 0
    }
  } catch (error) {
    ElMessage.error('获取勋章数据失败')
  } finally {
    loading.value = false
  }
}

const handleAchievementClick = async (achievement) => {
  currentAchievement.value = { ...achievement }
  dialogVisible.value = true

  if (achievement.isNew && achievement.obtained) {
    try {
      await markAchievementAsRead(achievement.id)
      achievement.isNew = false
      newCount.value = Math.max(0, newCount.value - 1)
    } catch (error) {
      console.error('标记已读失败', error)
    }
  }
}

onMounted(() => {
  fetchAchievements()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .stats {
    display: flex;
    align-items: center;
  }

  .ml-10 {
    margin-left: 10px;
  }
}

.achievement-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.achievement-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #ebeef5;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  &.obtained {
    border-color: #67c23a;
    background: linear-gradient(135deg, rgba(103, 194, 58, 0.05) 0%, rgba(103, 194, 58, 0.02) 100%);

    .achievement-icon {
      color: #67c23a;
    }
  }

  &.is-new {
    border-color: #e6a23c;
  }

  .achievement-icon {
    width: 80px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: #f5f7fa;
    color: #c0c4cc;
    margin-right: 15px;
    flex-shrink: 0;

    .locked {
      color: #c0c4cc;
    }
  }

  .achievement-info {
    flex: 1;
    min-width: 0;

    .achievement-name {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 5px;
    }

    .achievement-desc {
      font-size: 13px;
      color: #909399;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .achievement-badge {
    position: absolute;
    top: 10px;
    right: 10px;
  }
}

.achievement-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;

  .detail-icon {
    margin-bottom: 20px;

    .text-success {
      color: #67c23a;
    }

    .text-gray {
      color: #c0c4cc;
    }
  }

  .detail-info {
    width: 100%;

    .detail-name {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 10px;
    }

    .detail-desc {
      font-size: 14px;
      color: #606266;
      margin-bottom: 15px;
      line-height: 1.6;
    }

    .detail-req,
    .detail-reward,
    .detail-date {
      font-size: 14px;
      margin-bottom: 8px;
      text-align: left;

      .req-label {
        color: #909399;
        margin-right: 5px;
      }

      .text-success {
        color: #67c23a;
        font-weight: bold;
      }
    }
  }
}

.text-success {
  color: #67c23a;
}

.text-gray {
  color: #c0c4cc;
}
</style>
