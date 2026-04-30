<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>积分规则配置</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增配置
          </el-button>
        </div>
      </template>

      <div class="search-form mb-20">
        <el-form :inline="true" :model="searchForm">
          <el-form-item label="积分类型">
            <el-select v-model="searchForm.pointsType" placeholder="请选择积分类型" clearable @change="handleSearch">
              <el-option label="每日打卡" value="daily_check_in" />
              <el-option label="连续7天打卡" value="continuous_streak_7" />
              <el-option label="连续15天打卡" value="continuous_streak_15" />
              <el-option label="连续30天打卡" value="continuous_streak_30" />
              <el-option label="完成目标" value="goal_complete" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @change="handleSearch">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="configList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="pointsType" label="积分类型" width="180">
          <template #default="{ row }">
            <el-tag size="small">{{ row.pointsType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pointsTypeName" label="类型名称" width="150" />
        <el-table-column prop="points" label="积分值" width="100">
          <template #default="{ row }">
            <span class="text-success font-bold">+{{ row.points }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
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
      :title="isEdit ? '编辑积分配置' : '新增积分配置'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="积分类型" prop="pointsType" v-if="!isEdit">
          <el-select v-model="form.pointsType" placeholder="请选择积分类型" style="width: 100%">
            <el-option label="每日打卡" value="daily_check_in" />
            <el-option label="连续7天打卡" value="continuous_streak_7" />
            <el-option label="连续15天打卡" value="continuous_streak_15" />
            <el-option label="连续30天打卡" value="continuous_streak_30" />
            <el-option label="完成目标" value="goal_complete" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分类型" prop="pointsType" v-else>
          <el-input v-model="form.pointsType" disabled />
        </el-form-item>
        <el-form-item label="类型名称" prop="pointsTypeName">
          <el-input v-model="form.pointsTypeName" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="积分值" prop="points">
          <el-input-number v-model="form.points" :min="0" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getPointsConfigList,
  createPointsConfig,
  updatePointsConfig,
  deletePointsConfig,
  updatePointsConfigStatus
} from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const configList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  page: 1,
  size: 10,
  pointsType: null,
  status: null
})

const form = reactive({
  id: null,
  pointsType: '',
  pointsTypeName: '',
  points: 0,
  description: '',
  status: 1
})

const rules = {
  pointsType: [{ required: true, message: '请选择积分类型', trigger: 'change' }],
  pointsTypeName: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
  points: [{ required: true, message: '请输入积分值', trigger: 'blur' }]
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
    if (searchForm.pointsType) {
      params.pointsType = searchForm.pointsType
    }
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }

    const res = await getPointsConfigList(params)
    if (res.code === 200) {
      configList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取积分配置列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.page = 1
  fetchList()
}

const handleReset = () => {
  searchForm.pointsType = null
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.pointsType = ''
  form.pointsTypeName = ''
  form.points = 0
  form.description = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.pointsType = row.pointsType
  form.pointsTypeName = row.pointsTypeName
  form.points = row.points
  form.description = row.description || ''
  form.status = row.status
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updatePointsConfig(form)
    } else {
      res = await createPointsConfig(form)
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
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
  const action = newStatus === 1 ? '启用' : '禁用'

  ElMessageBox.confirm(`确定要${action}该积分配置吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await updatePointsConfigStatus(row.id, newStatus)
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
  ElMessageBox.confirm('确定要删除该积分配置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deletePointsConfig(row.id)
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

.text-success {
  color: #67c23a;
}

.font-bold {
  font-weight: bold;
}
</style>
