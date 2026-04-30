<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>体征数据管理</span>
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
          <el-form-item label="视力状态">
            <el-select v-model="searchForm.visionStatus" placeholder="全部" clearable>
              <el-option label="优秀" value="excellent" />
              <el-option label="良好" value="good" />
              <el-option label="一般" value="normal" />
              <el-option label="较差" value="poor" />
            </el-select>
          </el-form-item>
          <el-form-item label="睡眠质量">
            <el-select v-model="searchForm.sleepQuality" placeholder="全部" clearable>
              <el-option label="1分-非常差" :value="1" />
              <el-option label="2分-较差" :value="2" />
              <el-option label="3分-一般" :value="3" />
              <el-option label="4分-良好" :value="4" />
              <el-option label="5分-非常好" :value="5" />
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
            <el-tag v-if="row.visionStatus" :type="getVisionStatusTag(row.visionStatus)">
              {{ getVisionStatusName(row.visionStatus) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sleepQuality" label="睡眠质量" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.sleepQuality" :type="getSleepQualityTag(row.sleepQuality)">
              {{ getSleepQualityName(row.sleepQuality) }}
            </el-tag>
            <span v-else>-</span>
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
      :title="isEdit ? '编辑体征数据' : '添加体征数据'"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="用户ID" prop="userId" v-if="!isEdit">
          <el-input-number v-model="form.userId" :min="1" style="width: 100%" />
        </el-form-item>
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
  getVitalSignList,
  createVitalSign,
  updateVitalSign,
  deleteVitalSign
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
  visionStatus: '',
  sleepQuality: null,
  startDate: '',
  endDate: ''
})

const form = reactive({
  id: null,
  userId: null,
  weight: null,
  bodyFat: null,
  systolicPressure: null,
  diastolicPressure: null,
  visionStatus: '',
  sleepQuality: null,
  remark: ''
})

const rules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }]
}

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

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const getVisionStatusName = (status) => visionStatusMap[status]?.name || status
const getVisionStatusTag = (status) => visionStatusMap[status]?.tag || 'info'
const getSleepQualityName = (quality) => sleepQualityMap[quality]?.name || '-'
const getSleepQualityTag = (quality) => sleepQualityMap[quality]?.tag || 'info'

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
    if (searchForm.visionStatus) {
      params.visionStatus = searchForm.visionStatus
    }
    if (searchForm.sleepQuality !== null) {
      params.sleepQuality = searchForm.sleepQuality
    }
    if (searchForm.startDate) {
      params.startDate = searchForm.startDate
    }
    if (searchForm.endDate) {
      params.endDate = searchForm.endDate
    }
    
    const res = await getVitalSignList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取体征数据失败', error)
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
  searchForm.visionStatus = ''
  searchForm.sleepQuality = null
  searchForm.startDate = ''
  searchForm.endDate = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.userId = null
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
  form.userId = row.userId
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
  if (!isEdit.value) {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
  }

  submitLoading.value = true
  try {
    let res
    const data = {
      weight: form.weight,
      bodyFat: form.bodyFat,
      systolicPressure: form.systolicPressure,
      diastolicPressure: form.diastolicPressure,
      visionStatus: form.visionStatus || null,
      sleepQuality: form.sleepQuality,
      remark: form.remark
    }

    if (isEdit.value) {
      res = await updateVitalSign(form.id, data)
    } else {
      res = await createVitalSign(form.userId, data)
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
    await ElMessageBox.confirm('确定要删除该体征数据吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteVitalSign(row.id)
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
