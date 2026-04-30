<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>今日饮食记录</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加记录
          </el-button>
        </div>
      </template>

      <el-empty v-if="todayRecords.length === 0" description="暂无今日饮食记录" />

      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="record in todayRecords" :key="record.id">
          <el-card shadow="hover">
            <template #header>
              <div class="flex-between">
                <el-tag :type="getMealTypeTag(record.mealType)">
                  {{ getMealTypeName(record.mealType) }}
                </el-tag>
                <el-tag v-if="record.status === 1" type="success">已打卡</el-tag>
                <el-tag v-else type="info">未打卡</el-tag>
              </div>
            </template>
            <div class="record-info">
              <div v-if="record.dietTaste">
                <span class="label">饮食口味：</span>
                <span class="value">{{ getDietTasteName(record.dietTaste) }}</span>
              </div>
              <div v-if="record.avoidFoodCompliance !== null">
                <span class="label">忌口执行：</span>
                <el-tag :type="record.avoidFoodCompliance === 1 ? 'success' : 'danger'">
                  {{ record.avoidFoodCompliance === 1 ? '已执行' : '未执行' }}
                </el-tag>
              </div>
              <div v-if="record.remark">
                <span class="label">备注：</span>
                <span class="value">{{ record.remark }}</span>
              </div>
            </div>
            <div class="record-actions">
              <el-button type="primary" link @click="handleEdit(record)">编辑</el-button>
              <el-button type="danger" link @click="handleDelete(record)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>历史记录</span>
          <div class="filter-group">
            <el-select
              v-model="filterMealType"
              placeholder="全部餐次"
              clearable
              style="width: 100px; margin-right: 10px"
              @change="fetchRecordList"
            >
              <el-option label="早餐" value="breakfast" />
              <el-option label="午餐" value="lunch" />
              <el-option label="晚餐" value="dinner" />
            </el-select>
            <el-select
              v-model="filterDietTaste"
              placeholder="全部口味"
              clearable
              style="width: 100px; margin-right: 10px"
              @change="fetchRecordList"
            >
              <el-option label="清淡" value="light" />
              <el-option label="适中" value="moderate" />
              <el-option label="油腻" value="oily" />
            </el-select>
            <el-select
              v-model="filterStatus"
              placeholder="全部状态"
              clearable
              style="width: 100px"
              @change="fetchRecordList"
            >
              <el-option label="已打卡" :value="1" />
              <el-option label="未打卡" :value="0" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="recordList" v-loading="listLoading" stripe>
        <el-table-column prop="recordDate" label="日期" width="120" />
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
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination mt-20">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchRecordList"
          @current-change="fetchRecordList"
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
        <el-form-item label="餐次类型" prop="mealType" v-if="!isEdit">
          <el-select v-model="form.mealType" placeholder="请选择餐次类型" style="width: 100%">
            <el-option label="早餐" value="breakfast" />
            <el-option label="午餐" value="lunch" />
            <el-option label="晚餐" value="dinner" />
          </el-select>
        </el-form-item>
        <el-form-item label="餐次类型" v-else>
          <el-tag :type="getMealTypeTag(form.mealType)">
            {{ getMealTypeName(form.mealType) }}
          </el-tag>
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
  addDietRecord,
  updateDietRecord,
  deleteDietRecord,
  getTodayDietRecords,
  getDietRecordList
} from '@/api/dietRecord'

const todayRecords = ref([])
const recordList = ref([])
const listLoading = ref(false)
const filterMealType = ref('')
const filterDietTaste = ref('')
const filterStatus = ref(null)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  mealType: 'breakfast',
  dietTaste: '',
  avoidFoodCompliance: null,
  status: 1,
  remark: ''
})

const rules = {
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

const getMealTypeName = (type) => mealTypeMap[type]?.name || type
const getMealTypeTag = (type) => mealTypeMap[type]?.tag || 'info'
const getDietTasteName = (taste) => dietTasteMap[taste] || taste

const fetchTodayRecords = async () => {
  try {
    const res = await getTodayDietRecords()
    if (res.code === 200) {
      todayRecords.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取今日饮食记录失败', error)
  }
}

const fetchRecordList = async () => {
  listLoading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (filterMealType.value) {
      params.mealType = filterMealType.value
    }
    if (filterDietTaste.value) {
      params.dietTaste = filterDietTaste.value
    }
    if (filterStatus.value !== null) {
      params.status = filterStatus.value
    }

    const res = await getDietRecordList(params)
    if (res.code === 200) {
      recordList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取饮食记录列表失败', error)
  } finally {
    listLoading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
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
  form.mealType = row.mealType
  form.dietTaste = row.dietTaste || ''
  form.avoidFoodCompliance = row.avoidFoodCompliance
  form.status = row.status
  form.remark = row.remark || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updateDietRecord(form.id, {
        mealType: form.mealType,
        dietTaste: form.dietTaste || null,
        avoidFoodCompliance: form.avoidFoodCompliance,
        status: form.status,
        remark: form.remark
      })
    } else {
      res = await addDietRecord({
        mealType: form.mealType,
        dietTaste: form.dietTaste || null,
        avoidFoodCompliance: form.avoidFoodCompliance,
        status: form.status,
        remark: form.remark
      })
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
      dialogVisible.value = false
      fetchTodayRecords()
      fetchRecordList()
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
      fetchTodayRecords()
      fetchRecordList()
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
  fetchTodayRecords()
  fetchRecordList()
})
</script>

<style scoped lang="scss">
.filter-group {
  display: flex;
  align-items: center;
}

.record-info {
  .label {
    color: #909399;
    font-size: 14px;
  }
  
  .value {
    color: #303133;
    font-size: 14px;
  }
  
  > div {
    margin-bottom: 8px;
  }
}

.record-actions {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
