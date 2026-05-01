<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>留言板管理</span>
        </div>
      </template>

      <div class="search-form mb-20">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="用户昵称">
            <el-input
              v-model="searchForm.nickname"
              placeholder="请输入用户昵称"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @change="handleSearch">
              <el-option label="显示" :value="1" />
              <el-option label="隐藏" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="messageList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="用户昵称" width="150" />
        <el-table-column prop="content" label="留言内容" min-width="300">
          <template #default="{ row }">
            <el-tooltip :content="row.content" placement="top" effect="dark">
              <span>{{ truncateContent(row.content) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="Number(row.status) === 1 ? 'success' : 'danger'" size="small">
              {{ Number(row.status) === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="Number(row.status) === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ Number(row.status) === 1 ? '隐藏' : '显示' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <el-dialog
      :title="isEdit ? '编辑留言' : '查看留言'"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" v-if="isEdit">
        <el-form-item label="用户昵称">
          <el-input v-model="form.nickname" disabled />
        </el-form-item>
        <el-form-item label="留言内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入留言内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div v-else class="message-detail">
        <div class="detail-header">
          <div class="user-info">
            <el-avatar :size="40" icon="UserFilled" />
            <span class="nickname">{{ form.nickname }}</span>
          </div>
          <div class="detail-meta">
            <span>发布时间: {{ formatTime(form.createTime) }}</span>
            <span>更新时间: {{ formatTime(form.updateTime) }}</span>
          </div>
        </div>
        <div class="detail-content">
          <pre>{{ form.content }}</pre>
        </div>
        <div class="detail-status">
          <el-tag :type="Number(form.status) === 1 ? 'success' : 'danger'" size="small">
            {{ Number(form.status) === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </div>
      </div>
      <template #footer v-if="isEdit">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getMessageBoardList,
  updateMessageBoard,
  deleteMessageBoard,
  updateMessageBoardStatus
} from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const messageList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  page: 1,
  size: 10,
  nickname: '',
  status: null
})

const form = reactive({
  id: null,
  nickname: '',
  content: '',
  status: 1,
  createTime: '',
  updateTime: ''
})

const rules = {
  content: [
    { required: true, message: '请输入留言内容', trigger: 'blur' },
    { min: 1, max: 500, message: '留言内容长度在 1 到 500 个字符', trigger: 'blur' }
  ]
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const truncateContent = (content) => {
  if (!content) return ''
  return content.length > 50 ? content.substring(0, 50) + '...' : content
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: searchForm.page,
      size: searchForm.size
    }
    if (searchForm.nickname) {
      params.nickname = searchForm.nickname
    }
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }

    const res = await getMessageBoardList(params)
    if (res.code === 200) {
      messageList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取留言列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.page = 1
  fetchList()
}

const handleReset = () => {
  searchForm.nickname = ''
  searchForm.status = null
  handleSearch()
}

const handleView = (row) => {
  isEdit.value = false
  form.id = row.id
  form.nickname = row.nickname
  form.content = row.content
  form.status = Number(row.status)
  form.createTime = row.createTime
  form.updateTime = row.updateTime
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.nickname = row.nickname
  form.content = row.content
  form.status = Number(row.status)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const res = await updateMessageBoard({
      id: form.id,
      content: form.content,
      status: form.status
    })

    if (res.code === 200) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      fetchList()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

const handleToggleStatus = (row) => {
  const newStatus = Number(row.status) === 1 ? 0 : 1
  const action = newStatus === 1 ? '显示' : '隐藏'

  ElMessageBox.confirm(`确定要${action}该留言吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await updateMessageBoardStatus(row.id, newStatus)
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        fetchList()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该留言吗？删除后无法恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteMessageBoard(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchList()
      } else {
        ElMessage.error(res.message || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败，请稍后重试')
    }
  }).catch(() => {})
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

.message-detail {
  .detail-header {
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
    margin-bottom: 20px;

    .user-info {
      display: flex;
      align-items: center;
      margin-bottom: 10px;

      .nickname {
        margin-left: 12px;
        font-size: 16px;
        font-weight: 500;
        color: #303133;
      }
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
    margin-bottom: 15px;

    pre {
      margin: 0;
      font-family: inherit;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }

  .detail-status {
    padding-top: 15px;
    border-top: 1px solid #eee;
  }
}
</style>
