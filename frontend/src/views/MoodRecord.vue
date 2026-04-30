<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>今日心情</span>
          <el-button type="primary" @click="handleAdd" v-if="!todayRecord">
            <el-icon><Plus /></el-icon>
            记录心情
          </el-button>
          <el-button type="primary" @click="handleEdit(todayRecord)" v-else>
            <el-icon><Edit /></el-icon>
            编辑心情
          </el-button>
        </div>
      </template>

      <el-empty v-if="!todayRecord" description="还没有记录今日心情，快来记录吧~" />

      <div v-else class="mood-display">
        <div class="mood-icon" :class="getMoodIconClass(todayRecord.moodType)">
          <el-icon :size="80">{{ getMoodIcon(todayRecord.moodType) }}</el-icon>
        </div>
        <div class="mood-info">
          <div class="mood-type">
            <el-tag :type="getMoodTag(todayRecord.moodType)" size="large">
              {{ getMoodName(todayRecord.moodType) }}
            </el-tag>
          </div>
          <div v-if="todayRecord.remark" class="mood-remark">
            <span class="label">心情记录：</span>
            <span class="value">{{ todayRecord.remark }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>历史心情记录</span>
          <el-select
            v-model="filterMoodType"
            placeholder="全部心情"
            clearable
            style="width: 120px"
            @change="fetchRecordList"
          >
            <el-option label="非常开心" value="very_happy" />
            <el-option label="开心" value="happy" />
            <el-option label="一般" value="normal" />
            <el-option label="难过" value="sad" />
            <el-option label="生气" value="angry" />
          </el-select>
        </div>
      </template>

      <el-table :data="recordList" v-loading="listLoading" stripe>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="moodType" label="心情" width="150">
          <template #default="{ row }">
            <div class="mood-cell">
              <el-icon class="mood-icon-small" :class="getMoodIconClass(row.moodType)">
                {{ getMoodIcon(row.moodType) }}
              </el-icon>
              <el-tag :type="getMoodTag(row.moodType)">
                {{ getMoodName(row.moodType) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="心情记录" min-width="200" />
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
      :title="isEdit ? '编辑心情' : '记录心情'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="选择心情" prop="moodType">
          <div class="mood-selector">
            <div
              v-for="mood in moodOptions"
              :key="mood.value"
              class="mood-option"
              :class="{ active: form.moodType === mood.value }"
              @click="form.moodType = mood.value"
            >
              <el-icon :size="40" class="mood-option-icon">{{ getMoodIcon(mood.value) }}</el-icon>
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
import { ref, reactive, onMounted, markRaw } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Star,
  ChatLineRound,
  User,
  CircleClose,
  Warning
} from '@element-plus/icons-vue'
import {
  addMoodRecord,
  updateMoodRecord,
  deleteMoodRecord,
  getTodayMoodRecord,
  getMoodRecordList
} from '@/api/moodRecord'

const todayRecord = ref(null)
const recordList = ref([])
const listLoading = ref(false)
const filterMoodType = ref('')
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
  moodType: 'happy',
  remark: ''
})

const rules = {
  moodType: [{ required: true, message: '请选择心情', trigger: 'change' }]
}

const moodOptions = [
  { value: 'very_happy', label: '非常开心' },
  { value: 'happy', label: '开心' },
  { value: 'normal', label: '一般' },
  { value: 'sad', label: '难过' },
  { value: 'angry', label: '生气' }
]

const moodTypeMap = {
  very_happy: { name: '非常开心', tag: 'success', icon: markRaw(Star) },
  happy: { name: '开心', tag: 'primary', icon: markRaw(ChatLineRound) },
  normal: { name: '一般', tag: 'info', icon: markRaw(User) },
  sad: { name: '难过', tag: 'warning', icon: markRaw(CircleClose) },
  angry: { name: '生气', tag: 'danger', icon: markRaw(Warning) }
}

const getMoodName = (type) => moodTypeMap[type]?.name || type
const getMoodTag = (type) => moodTypeMap[type]?.tag || 'info'
const getMoodIcon = (type) => moodTypeMap[type]?.icon || markRaw(User)
const getMoodIconClass = (type) => `mood-${type}`

const fetchTodayRecord = async () => {
  try {
    const res = await getTodayMoodRecord()
    if (res.code === 200) {
      todayRecord.value = res.data.moodRecord || null
    }
  } catch (error) {
    console.error('获取今日心情记录失败', error)
  }
}

const fetchRecordList = async () => {
  listLoading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (filterMoodType.value) {
      params.moodType = filterMoodType.value
    }

    const res = await getMoodRecordList(params)
    if (res.code === 200) {
      recordList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取心情记录列表失败', error)
  } finally {
    listLoading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.moodType = 'happy'
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.moodType = row.moodType
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
      res = await updateMoodRecord(form.id, {
        moodType: form.moodType,
        remark: form.remark
      })
    } else {
      res = await addMoodRecord({
        moodType: form.moodType,
        remark: form.remark
      })
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '记录成功')
      dialogVisible.value = false
      fetchTodayRecord()
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
    await ElMessageBox.confirm('确定要删除该心情记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteMoodRecord(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchTodayRecord()
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
  fetchTodayRecord()
  fetchRecordList()
})
</script>

<style scoped lang="scss">
.mood-display {
  display: flex;
  align-items: center;
  gap: 40px;
  padding: 20px;

  .mood-icon {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;

    &.mood-very_happy {
      background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
    }
    &.mood-happy {
      background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    }
    &.mood-normal {
      background: linear-gradient(135deg, #909399 0%, #b4b4b4 100%);
    }
    &.mood-sad {
      background: linear-gradient(135deg, #e6a23c 0%, #f3d19e 100%);
    }
    &.mood-angry {
      background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
    }
  }

  .mood-info {
    flex: 1;

    .mood-type {
      margin-bottom: 15px;
    }

    .mood-remark {
      padding: 15px;
      background: #f5f7fa;
      border-radius: 8px;

      .label {
        color: #909399;
      }

      .value {
        color: #303133;
      }
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
    flex-direction: column;
    align-items: center;
    padding: 15px 10px;
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

    .mood-option-icon {
      margin-bottom: 8px;
    }

    .mood-option-label {
      font-size: 12px;
      color: #606266;
    }
  }
}

.mood-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  .mood-icon-small {
    font-size: 20px;
  }
}

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
