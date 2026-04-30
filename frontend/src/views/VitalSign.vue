<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>今日体征数据</span>
          <el-button type="primary" @click="handleAdd" v-if="!todayRecord">
            <el-icon><Plus /></el-icon>
            添加记录
          </el-button>
          <el-button type="primary" @click="handleEdit(todayRecord)" v-else>
            <el-icon><Edit /></el-icon>
            编辑记录
          </el-button>
        </div>
      </template>

      <el-empty v-if="!todayRecord" description="暂无今日体征数据记录" />

      <div v-else class="today-record">
        <el-row :gutter="20">
          <el-col :span="6" v-for="item in vitalSignItems" :key="item.key">
            <div class="stat-item">
              <div class="stat-label">{{ item.label }}</div>
              <div class="stat-value" :class="item.valueClass">
                {{ todayRecord[item.key] !== null ? todayRecord[item.key] : '-' }}
                <span class="stat-unit">{{ item.unit }}</span>
              </div>
            </div>
          </el-col>
        </el-row>
        <div v-if="todayRecord.remark" class="remark-section mt-20">
          <span class="label">备注：</span>
          <span class="value">{{ todayRecord.remark }}</span>
        </div>
      </div>
    </el-card>

    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>历史记录</span>
          <el-select
            v-model="filterVisionStatus"
            placeholder="视力状态"
            clearable
            style="width: 120px"
            @change="fetchRecordList"
          >
            <el-option label="优秀" value="excellent" />
            <el-option label="良好" value="good" />
            <el-option label="一般" value="normal" />
            <el-option label="较差" value="poor" />
          </el-select>
        </div>
      </template>

      <el-table :data="recordList" v-loading="listLoading" stripe>
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="weight" label="体重(kg)" width="100">
          <template #default="{ row }">
            {{ row.weight !== null ? row.weight : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="bodyFat" label="体脂率(%)" width="100">
          <template #default="{ row }">
            {{ row.bodyFat !== null ? row.bodyFat : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="systolicPressure" label="收缩压" width="100">
          <template #default="{ row }">
            {{ row.systolicPressure !== null ? row.systolicPressure : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="diastolicPressure" label="舒张压" width="100">
          <template #default="{ row }">
            {{ row.diastolicPressure !== null ? row.diastolicPressure : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="visionStatus" label="视力状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getVisionStatusTag(row.visionStatus)">
              {{ getVisionStatusName(row.visionStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sleepQuality" label="睡眠质量" width="100">
          <template #default="{ row }">
            <el-tag :type="getSleepQualityTag(row.sleepQuality)">
              {{ getSleepQualityName(row.sleepQuality) }}
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
      :title="isEdit ? '编辑体征数据' : '添加体征数据'"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="体重(kg)" prop="weight">
              <el-input-number
                v-model="form.weight"
                :min="0"
                :max="500"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="体脂率(%)" prop="bodyFat">
              <el-input-number
                v-model="form.bodyFat"
                :min="0"
                :max="100"
                :precision="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收缩压(mmHg)" prop="systolicPressure">
              <el-input-number
                v-model="form.systolicPressure"
                :min="0"
                :max="300"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="舒张压(mmHg)" prop="diastolicPressure">
              <el-input-number
                v-model="form.diastolicPressure"
                :min="0"
                :max="200"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="视力状态" prop="visionStatus">
              <el-select v-model="form.visionStatus" placeholder="请选择视力状态" clearable style="width: 100%">
                <el-option label="优秀" value="excellent" />
                <el-option label="良好" value="good" />
                <el-option label="一般" value="normal" />
                <el-option label="较差" value="poor" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="睡眠质量" prop="sleepQuality">
              <el-select v-model="form.sleepQuality" placeholder="请选择睡眠质量" clearable style="width: 100%">
                <el-option :label="sleepQualityMap[1].name" :value="1" />
                <el-option :label="sleepQualityMap[2].name" :value="2" />
                <el-option :label="sleepQualityMap[3].name" :value="3" />
                <el-option :label="sleepQualityMap[4].name" :value="4" />
                <el-option :label="sleepQualityMap[5].name" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
  addVitalSign,
  updateVitalSign,
  deleteVitalSign,
  getTodayVitalSign,
  getVitalSignList
} from '@/api/vitalSign'

const todayRecord = ref(null)
const recordList = ref([])
const listLoading = ref(false)
const filterVisionStatus = ref('')
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
  weight: null,
  bodyFat: null,
  systolicPressure: null,
  diastolicPressure: null,
  visionStatus: '',
  sleepQuality: null,
  remark: ''
})

const rules = {}

const vitalSignItems = [
  { key: 'weight', label: '体重', unit: 'kg', valueClass: 'text-primary' },
  { key: 'bodyFat', label: '体脂率', unit: '%', valueClass: 'text-warning' },
  { key: 'systolicPressure', label: '收缩压', unit: 'mmHg', valueClass: 'text-danger' },
  { key: 'diastolicPressure', label: '舒张压', unit: 'mmHg', valueClass: 'text-danger' },
  { key: 'visionStatus', label: '视力状态', unit: '', valueClass: 'text-success', isEnum: true },
  { key: 'sleepQuality', label: '睡眠质量', unit: '分', valueClass: 'text-info', isEnum: true }
]

const visionStatusMap = {
  excellent: { name: '优秀', tag: 'success' },
  good: { name: '良好', tag: 'primary' },
  normal: { name: '一般', tag: 'warning' },
  poor: { name: '较差', tag: 'danger' }
}

const sleepQualityMap = {
  1: { name: '1分 - 非常差', tag: 'danger' },
  2: { name: '2分 - 较差', tag: 'warning' },
  3: { name: '3分 - 一般', tag: 'info' },
  4: { name: '4分 - 良好', tag: 'primary' },
  5: { name: '5分 - 非常好', tag: 'success' }
}

const getVisionStatusName = (status) => visionStatusMap[status]?.name || status
const getVisionStatusTag = (status) => visionStatusMap[status]?.tag || 'info'
const getSleepQualityName = (quality) => sleepQualityMap[quality]?.name || '-'
const getSleepQualityTag = (quality) => sleepQualityMap[quality]?.tag || 'info'

const fetchTodayRecord = async () => {
  try {
    const res = await getTodayVitalSign()
    if (res.code === 200) {
      todayRecord.value = res.data.vitalSign || null
    }
  } catch (error) {
    console.error('获取今日体征数据失败', error)
  }
}

const fetchRecordList = async () => {
  listLoading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }
    if (filterVisionStatus.value) {
      params.visionStatus = filterVisionStatus.value
    }

    const res = await getVitalSignList(params)
    if (res.code === 200) {
      recordList.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('获取体征数据列表失败', error)
  } finally {
    listLoading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.weight = null
  form.bodyFat = null
  form.systolicPressure = null
  form.diastolicPressure = null
  form.visionStatus = ''
  form.sleepQuality = null
  form.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.weight = row.weight
  form.bodyFat = row.bodyFat
  form.systolicPressure = row.systolicPressure
  form.diastolicPressure = row.diastolicPressure
  form.visionStatus = row.visionStatus || ''
  form.sleepQuality = row.sleepQuality
  form.remark = row.remark || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updateVitalSign(form.id, {
        weight: form.weight,
        bodyFat: form.bodyFat,
        systolicPressure: form.systolicPressure,
        diastolicPressure: form.diastolicPressure,
        visionStatus: form.visionStatus || null,
        sleepQuality: form.sleepQuality,
        remark: form.remark
      })
    } else {
      res = await addVitalSign({
        weight: form.weight,
        bodyFat: form.bodyFat,
        systolicPressure: form.systolicPressure,
        diastolicPressure: form.diastolicPressure,
        visionStatus: form.visionStatus || null,
        sleepQuality: form.sleepQuality,
        remark: form.remark
      })
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
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
    await ElMessageBox.confirm('确定要删除该体征数据记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteVitalSign(row.id)
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
.today-record {
  .stat-item {
    text-align: center;
    padding: 20px;
    background: #f5f7fa;
    border-radius: 8px;

    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 10px;
    }

    .stat-value {
      font-size: 28px;
      font-weight: bold;

      .stat-unit {
        font-size: 14px;
        color: #909399;
        margin-left: 5px;
      }
    }

    .text-primary {
      color: #409eff;
    }

    .text-warning {
      color: #e6a23c;
    }

    .text-danger {
      color: #f56c6c;
    }

    .text-success {
      color: #67c23a;
    }

    .text-info {
      color: #909399;
    }
  }
}

.remark-section {
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

.pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
