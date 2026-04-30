<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>公告管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            发布公告
          </el-button>
        </div>
      </template>

      <div class="search-form mb-20">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="标题">
            <el-input
              v-model="searchForm.title"
              placeholder="请输入标题关键字"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @change="handleSearch">
              <el-option label="上架" :value="1" />
              <el-option label="下架" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="announcementList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="publisherName" label="发布者" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
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
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
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
      :title="isEdit ? '编辑公告' : '发布公告'"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog title="公告详情" v-model="viewDialogVisible" width="600px">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getAnnouncementList,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  updateAnnouncementStatus
} from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const announcementList = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentAnnouncement = ref({})

const searchForm = reactive({
  page: 1,
  size: 10,
  title: '',
  status: null
})

const form = reactive({
  id: null,
  title: '',
  content: '',
  status: 1
})

const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容长度在 10 到 2000 个字符', trigger: 'blur' }
  ]
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: searchForm.page,
      size: searchForm.size
    }
    if (searchForm.title) {
      params.title = searchForm.title
    }
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }

    const res = await getAnnouncementList(params)
    if (res.code === 200) {
      announcementList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取公告列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.page = 1
  fetchList()
}

const handleReset = () => {
  searchForm.title = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.title = ''
  form.content = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.content = row.content
  form.status = row.status
  dialogVisible.value = true
}

const handleView = (row) => {
  currentAnnouncement.value = {
    ...row,
    content: row.content || ''
  }
  viewDialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updateAnnouncement(form)
    } else {
      res = await createAnnouncement(form)
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
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
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'

  ElMessageBox.confirm(`确定要${action}该公告吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await updateAnnouncementStatus(row.id, newStatus)
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
  ElMessageBox.confirm('确定要删除该公告吗？删除后无法恢复。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteAnnouncement(row.id)
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
