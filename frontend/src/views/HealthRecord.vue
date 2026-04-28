<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>今日健康记录</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加记录
          </el-button>
        </div>
      </template>

      <el-empty v-if="todayRecords.length === 0" description="暂无今日健康记录" />

      <el-row :gutter="20" v-else>
        <el-col :span="8" v-for="record in todayRecords" :key="record.id">
          <el-card shadow="hover">
            <template #header>
              <div class="flex-between">
                <el-tag :type="getRecordTypeTag(record.recordType)">
                  {{ getRecordTypeName(record.recordType) }}
                </el-tag>
                <el-tag v-if="record.status === 1" type="success">已完成</el-tag>
                <el-tag v-else type="info">未完成</el-tag>
              </div>
            </template>
            <div class="record-info">
              <div v-if="record.quantity !== null">
                <span class="label">数量：</span>
                <span class="value">{{ record.quantity }} {{ getRecordUnit(record.recordType) }}</span>
              </div>
              <div v-if="record.duration !== null">
                <span class="label">时长：</span>
                <span class="value">{{ record.duration }} 分钟</span>
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
          <el-select
            v-model="filterType"
            placeholder="全部类型"
            clearable
            style="width: 120px"
            @change="fetchRecordList"
          >
            <el-option label="运动" value="exercise" />
            <el-option label="饮水" value="water" />
            <el-option label="早睡" value="sleep_early" />
          </el-select>
        </div>
      </template>

      <el-table :data="recordList" v-loading="listLoading" stripe>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="recordType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ getRecordTypeName(row.recordType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量">
          <template #default="{ row }">
            {{ row.quantity !== null ? row.quantity + ' ' + getRecordUnit(row.recordType) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)">
          <template #default="{ row }">
            {{ row.duration !== null ? row.duration : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">已完成</el-tag>
            <el-tag v-else type="info">未完成</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="150" fixed="right">
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
      :title="isEdit ? '编辑记录' : '添加记录'"
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="记录类型" prop="recordType">
          <el-select v-model="form.recordType" placeholder="请选择记录类型" style="width: 100%">
            <el-option label="运动" value="exercise" />
            <el-option label="饮水" value="water" />
            <el-option label="早睡" value="sleep_early" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number
            v-model="form.quantity"
            :min="0"
            :max="10000"
            style="width: 100%"
          />
          <span class="unit-text">{{ getRecordUnit(form.recordType) }}</span>
        </el-form-item>
        <el-form-item label="时长" prop="duration">
          <el-input-number
            v-model="form.duration"
            :min="0"
            :max="1440"
            style="width: 100%"
          />
          <span class="unit-text">分钟</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">已完成</el-radio>
            <el-radio :label="0">未完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
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
  addHealthRecord,
  updateHealthRecord,
  deleteHealthRecord,
  getTodayRecords,
  getHealthRecordList
} from '@/api/healthRecord'

const todayRecords = ref([])
const recordList = ref([])
const listLoading = ref(false)
const filterType = ref('')
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
  recordType: 'exercise',
  quantity: null,
  duration: null,
  status: 1,
  remark: ''
})

const rules = {
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const recordTypeMap = {
  exercise: { name: '运动', tag: 'warning', unit: '次' },
  water: { name: '饮水', tag: 'primary', unit: '毫升' },
  sleep_early: { name: '早睡', tag: 'success', unit: '' }
}

const getRecordTypeName = (type) => recordTypeMap[type]?.name || type
const getRecordTypeTag = (type) => recordTypeMap[type]?.tag || 'info'
const getRecordUnit = (type) => recordTypeMap[type]?.unit || ''

const fetchTodayRecords = async () => {
  try {
    const res = await getTodayRecords()
    if (res.code === 200) {
      todayRecords.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取今日记录失败', error)
  }
}

const fetchRecordList = async () => {
  listLoading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (filterType.value) {
      params.recordType = filterType.value
    }

    const res = await getHealthRecordList(params)
    if (res.code === 200) {
      recordList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取记录列表失败', error)
  } finally {
    listLoading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.recordType = 'exercise'
  form.quantity = null
  form.duration = null
  form.status = 1
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.recordType = row.recordType
  form.quantity = row.quantity
  form.duration = row.duration
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
      res = await updateHealthRecord(form.id, {
        quantity: form.quantity,
        duration: form.duration,
        status: form.status,
        remark: form.remark
      })
    } else {
      res = await addHealthRecord({
        recordType: form.recordType,
        quantity: form.quantity,
        duration: form.duration,
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
    await ElMessageBox.confirm('确定要删除该记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteHealthRecord(row.id)
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

.unit-text {
  margin-left: 10px;
  color: #909399;
}
</style>
