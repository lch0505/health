<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>饮食记录管理</span>
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
          <el-form-item label="餐次类型">
            <el-select v-model="searchForm.mealType" placeholder="全部" clearable>
              <el-option label="早餐" value="breakfast" />
              <el-option label="午餐" value="lunch" />
              <el-option label="晚餐" value="dinner" />
            </el-select>
          </el-form-item>
          <el-form-item label="饮食口味">
            <el-select v-model="searchForm.dietTaste" placeholder="全部" clearable>
              <el-option label="清淡" value="light" />
              <el-option label="适中" value="moderate" />
              <el-option label="油腻" value="oily" />
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
        <el-table-column prop="mealType" label="餐次" width="100">
          <template #default="{ row }">
            <el-tag :type="getMealTypeTag(row.mealType)">
              {{ getMealTypeName(row.mealType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dietTaste" label="饮食口味" width="100">
          <template #default="{ row }">
            <span v-if="row.dietTaste">{{ getDietTasteName(row.dietTaste) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="avoidFoodCompliance" label="忌口执行" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.avoidFoodCompliance !== null" :type="row.avoidFoodCompliance === 1 ? 'success' : 'danger'">
              {{ row.avoidFoodCompliance === 1 ? '已执行' : '未执行' }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="打卡状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已打卡' : '未打卡' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="100" show-overflow-tooltip />
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
      :title="isEdit ? '编辑饮食记录' : '添加饮食记录'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="用户ID" prop="userId" v-if="!isEdit">
          <el-input-number v-model="form.userId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="餐次类型" prop="mealType">
          <el-select v-model="form.mealType" placeholder="请选择餐次类型" style="width: 100%">
            <el-option label="早餐" value="breakfast" />
            <el-option label="午餐" value="lunch" />
            <el-option label="晚餐" value="dinner" />
          </el-select>
        </el-form-item>
        <el-form-item label="饮食口味" prop="dietTaste">
          <el-select v-model="form.dietTaste" placeholder="请选择饮食口味" clearable style="width: 100%">
            <el-option label="清淡" value="light" />
            <el-option label="适中" value="moderate" />
            <el-option label="油腻" value="oily" />
          </el-select>
        </el-form-item>
        <el-form-item label="忌口执行" prop="avoidFoodCompliance">
          <el-radio-group v-model="form.avoidFoodCompliance">
            <el-radio :label="1">已执行</el-radio>
            <el-radio :label="0">未执行</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="打卡状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">已打卡</el-radio>
            <el-radio :label="0">未打卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注（可选）"
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
  getDietRecordList,
  createDietRecord,
  updateDietRecord,
  deleteDietRecord
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
  mealType: '',
  dietTaste: '',
  startDate: '',
  endDate: ''
})

const form = reactive({
  id: null,
  userId: null,
  mealType: 'breakfast',
  dietTaste: '',
  avoidFoodCompliance: null,
  status: 1,
  remark: ''
})

const rules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  mealType: [{ required: true, message: '请选择餐次类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择打卡状态', trigger: 'change' }]
}

const mealTypeMap = {
  breakfast: { name: '早餐', tag: 'success' },
  lunch: { name: '午餐', tag: 'warning' },
  dinner: { name: '晚餐', tag: 'primary' }
}

const dietTasteMap = {
  light: '清淡',
  moderate: '适中',
  oily: '油腻'
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const getMealTypeName = (type) => mealTypeMap[type]?.name || type
const getMealTypeTag = (type) => mealTypeMap[type]?.tag || 'info'
const getDietTasteName = (taste) => dietTasteMap[taste] || taste

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
    if (searchForm.mealType) {
      params.mealType = searchForm.mealType
    }
    if (searchForm.dietTaste) {
      params.dietTaste = searchForm.dietTaste
    }
    if (searchForm.startDate) {
      params.startDate = searchForm.startDate
    }
    if (searchForm.endDate) {
      params.endDate = searchForm.endDate
    }
    
    const res = await getDietRecordList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取饮食记录失败', error)
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
  searchForm.mealType = ''
  searchForm.dietTaste = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.userId = null
  form.mealType = 'breakfast'
  form.dietTaste = ''
  form.avoidFoodCompliance = null
  form.status = 1
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.userId = row.userId
  form.mealType = row.mealType
  form.dietTaste = row.dietTaste || ''
  form.avoidFoodCompliance = row.avoidFoodCompliance
  form.status = row.status
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
      mealType: form.mealType,
      dietTaste: form.dietTaste || null,
      avoidFoodCompliance: form.avoidFoodCompliance,
      status: form.status,
      remark: form.remark
    }

    if (isEdit.value) {
      res = await updateDietRecord(form.id, data)
    } else {
      res = await createDietRecord(form.userId, data)
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
    await ElMessageBox.confirm('确定要删除该饮食记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteDietRecord(row.id)
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
</style>
