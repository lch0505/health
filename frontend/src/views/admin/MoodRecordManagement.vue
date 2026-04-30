<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>情绪记录管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加记录
          </el-button>
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
          <el-form-item label="心情类型">
            <el-select v-model="searchForm.moodType" placeholder="全部" clearable>
              <el-option label="非常开心" value="very_happy" />
              <el-option label="开心" value="happy" />
              <el-option label="一般" value="normal" />
              <el-option label="难过" value="sad" />
              <el-option label="生气" value="angry" />
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
        <el-table-column prop="moodType" label="心情" width="150">
          <template #default="{ row }">
            <el-tag :type="getMoodTag(row.moodType)">
              {{ getMoodName(row.moodType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="心情记录" min-width="200" />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog
      :title="isEdit ? '编辑心情记录' : '添加心情记录'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户ID" prop="userId" v-if="!isEdit">
          <el-input-number v-model="form.userId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="选择心情" prop="moodType">
          <div class="mood-selector">
            <div
              v-for="mood in moodOptions"
              :key="mood.value"
              class="mood-option"
              :class="{ active: form.moodType === mood.value }"
              @click="form.moodType = mood.value"
            >
              <span class="mood-option-label">{{ mood.label }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="心情记录" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="记录一下今天的心情吧（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
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
  getMoodRecordList,
  createMoodRecord,
  updateMoodRecord,
  deleteMoodRecord
} from '@/api/admin'

const loading = ref(false)
const total = ref(0)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  page: 1,
  size: 10,
  userId: null,
  moodType: '',
  startDate: '',
  endDate: ''
})

const form = reactive({
  id: null,
  userId: null,
  moodType: 'happy',
  remark: ''
})

const rules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  moodType: [{ required: true, message: '请选择心情', trigger: 'change' }]
}

const moodOptions = [
  { value: 'very_happy', label: '非常开心', tag: 'success' },
  { value: 'happy', label: '开心', tag: 'primary' },
  { value: 'normal', label: '一般', tag: 'info' },
  { value: 'sad', label: '难过', tag: 'warning' },
  { value: 'angry', label: '生气', tag: 'danger' }
]

const moodTypeMap = {
  very_happy: { name: '非常开心', tag: 'success' },
  happy: { name: '开心', tag: 'primary' },
  normal: { name: '一般', tag: 'info' },
  sad: { name: '难过', tag: 'warning' },
  angry: { name: '生气', tag: 'danger' }
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const getMoodName = (type) => moodTypeMap[type]?.name || type
const getMoodTag = (type) => moodTypeMap[type]?.tag || 'info'

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
    if (searchForm.moodType) {
      params.moodType = searchForm.moodType
    }
    if (searchForm.startDate) {
      params.startDate = searchForm.startDate
    }
    if (searchForm.endDate) {
      params.endDate = searchForm.endDate
    }
    
    const res = await getMoodRecordList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取情绪记录失败', error)
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
  searchForm.moodType = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.userId = null
  form.moodType = 'happy'
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.userId = row.userId
  form.moodType = row.moodType
  form.remark = row.remark || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!isEdit.value) {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
  }

  submitLoading.value = true
  try {
    let res
    const data = {
      moodType: form.moodType,
      remark: form.remark
    }

    if (isEdit.value) {
      res = await updateMoodRecord(form.id, data)
    } else {
      res = await createMoodRecord(form.userId, data)
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该心情记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteMoodRecord(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请稍后重试')
    }
  }
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

.mood-selector {
  display: flex;
  justify-content: space-between;
  gap: 10px;

  .mood-option {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 12px 8px;
    border: 2px solid #dcdfe6;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
    }

    &.active {
      border-color: #409eff;
      background: #ecf5ff;
    }

    .mood-option-label {
      font-size: 13px;
      color: #606266;
    }
  }
}
</style>
