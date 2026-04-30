<template>
  <div class="page-container">
    <el-card class="announcement-card">
      <template #header>
        <div class="flex-between">
          <span>公告通知</span>
        </div>
      </template>

      <div class="announcement-list" v-loading="loading">
        <div
          v-for="item in announcementList"
          :key="item.id"
          class="announcement-item"
          @click="handleViewDetail(item)"
        >
          <div class="announcement-title">
            <el-icon class="title-icon"><Bell /></el-icon>
            <span>{{ item.title }}</span>
            <el-tag v-if="isNew(item.createTime)" type="danger" size="small" class="new-tag">新</el-tag>
          </div>
          <div class="announcement-meta">
            <span class="publisher">发布者: {{ item.publisherName }}</span>
            <span class="time">{{ formatTime(item.createTime) }}</span>
          </div>
          <div class="announcement-preview">{{ truncateContent(item.content) }}</div>
        </div>

        <el-empty v-if="announcementList.length === 0 && !loading" description="暂无公告" />
      </div>

      <div class="pagination mt-20">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <el-dialog title="公告详情" v-model="detailDialogVisible" width="600px">
      <div class="announcement-detail">
        <div class="detail-header">
          <h3>{{ currentAnnouncement.title }}</h3>
          <div class="detail-meta">
            <span>发布者: {{ currentAnnouncement.publisherName }}</span>
            <span>发布时间: {{ formatTime(currentAnnouncement.createTime) }}</span>
          </div>
        </div>
        <div class="detail-content">
          <pre>{{ currentAnnouncement.content }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { getAnnouncementList } from '@/api/announcement'
import dayjs from 'dayjs'

const loading = ref(false)
const detailDialogVisible = ref(false)
const announcementList = ref([])
const currentAnnouncement = ref({})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const isNew = (createTime) => {
  if (!createTime) return false
  const now = dayjs()
  const createDate = dayjs(createTime)
  return now.diff(createDate, 'day') <= 3
}

const truncateContent = (content) => {
  if (!content) return ''
  const plainText = content.replace(/<[^>]*>/g, '')
  return plainText.length > 100 ? plainText.substring(0, 100) + '...' : plainText
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }

    const res = await getAnnouncementList(params)
    if (res.code === 200) {
      announcementList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取公告列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleViewDetail = (item) => {
  currentAnnouncement.value = {
    ...item,
    content: item.content || ''
  }
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped lang="scss">
.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-20 {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.announcement-list {
  .announcement-item {
    padding: 15px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 15px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      border-color: #409eff;
    }

    .announcement-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 8px;

      .title-icon {
        margin-right: 8px;
        color: #409eff;
      }

      .new-tag {
        margin-left: 10px;
      }
    }

    .announcement-meta {
      font-size: 13px;
      color: #909399;
      margin-bottom: 10px;

      .publisher {
        margin-right: 20px;
      }
    }

    .announcement-preview {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;
    }
  }
}

.announcement-detail {
  .detail-header {
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
    margin-bottom: 20px;

    h3 {
      margin: 0 0 10px 0;
      font-size: 18px;
      color: #303133;
    }

    .detail-meta {
      font-size: 14px;
      color: #909399;

      span {
        margin-right: 20px;
      }
    }
  }

  .detail-content {
    font-size: 15px;
    line-height: 1.8;
    color: #303133;
    white-space: pre-wrap;
    word-break: break-word;

    pre {
      margin: 0;
      font-family: inherit;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }
}
</style>
