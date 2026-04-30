<template>
  <div class="page-container">
    <el-card class="card-container">
      <template #header>
        <div class="flex-between">
          <span>勋章管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增勋章
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="勋章定义" name="achievement">
          <div class="search-form mb-20">
            <el-form :inline="true" :model="searchForm">
              <el-form-item label="名称">
                <el-input
                  v-model="searchForm.name"
                  placeholder="请输入勋章名称"
                  clearable
                  @keyup.enter="handleSearch"
                />
              </el-form-item>
              <el-form-item label="状态">
                <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
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

          <el-table :data="achievementList" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="achievementCode" label="代码" width="150" />
            <el-table-column prop="name" label="名称" width="120" />
            <el-table-column prop="description" label="描述" min-width="150" />
            <el-table-column prop="requirementDescription" label="获得条件" min-width="150" />
            <el-table-column prop="pointsReward" label="奖励积分" width="100">
              <template #default="{ row }">
                <span class="text-success">+{{ row.pointsReward }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-switch
                  v-model="row.status"
                  :active-value="1"
                  :inactive-value="0"
                  @change="handleStatusChange(row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link @click="handleEdit(row)">
                  编辑
                </el-button>
                <el-button type="danger" link @click="handleDelete(row)">
                  删除
                </el-button>
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
              @size-change="fetchAchievements"
              @current-change="fetchAchievements"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="用户勋章" name="user">
          <div class="search-form mb-20">
            <el-form :inline="true" :model="userSearchForm">
              <el-form-item label="用户ID">
                <el-input
                  v-model.number="userSearchForm.userId"
                  placeholder="请输入用户ID"
                  clearable
                  @keyup.enter="handleUserSearch"
                />
              </el-form-item>
              <el-form-item label="勋章ID">
                <el-input
                  v-model.number="userSearchForm.achievementId"
                  placeholder="请输入勋章ID"
                  clearable
                  @keyup.enter="handleUserSearch"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUserSearch">搜索</el-button>
                <el-button @click="handleUserReset">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <div class="mb-20">
            <el-button type="success" @click="handleGrant">
              <el-icon><Trophy /></el-icon>
              授予勋章
            </el-button>
          </div>

          <el-table :data="userAchievementList" v-loading="userLoading" stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="userId" label="用户ID" width="100" />
            <el-table-column prop="achievementName" label="勋章名称" width="150" />
            <el-table-column prop="achievementDescription" label="勋章描述" min-width="150" />
            <el-table-column prop="achievementPointsReward" label="奖励积分" width="100">
              <template #default="{ row }">
                <span class="text-success">+{{ row.achievementPointsReward }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="obtainDate" label="获得日期" width="120" />
            <el-table-column prop="isNew" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.isNew === 1 ? 'warning' : 'info'" size="small">
                  {{ row.isNew === 1 ? '新获得' : '已读' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleRevoke(row)">
                  撤销
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination mt-20">
            <el-pagination
              v-model:current-page="userSearchForm.page"
              v-model:page-size="userSearchForm.size"
              :page-sizes="[10, 20, 50]"
              :total="userTotal"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="fetchUserAchievements"
              @current-change="fetchUserAchievements"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="勋章代码" prop="achievementCode">
          <el-input v-model="form.achievementCode" placeholder="请输入勋章代码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入勋章名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="2"
            placeholder="请输入勋章描述"
          />
        </el-form-item>
        <el-form-item label="条件类型" prop="requirementType">
          <el-select v-model="form.requirementType" placeholder="请选择条件类型" style="width: 100%">
            <el-option label="连续天数" value="streak" />
            <el-option label="总次数" value="total" />
            <el-option label="月度统计" value="monthly" />
          </el-select>
        </el-form-item>
        <el-form-item label="条件值" prop="requirementValue">
          <el-input-number v-model="form.requirementValue" :min="1" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="条件描述" prop="requirementDescription">
          <el-input
            v-model="form.requirementDescription"
            type="textarea"
            :rows="2"
            placeholder="请输入条件描述"
          />
        </el-form-item>
        <el-form-item label="奖励积分" prop="pointsReward">
          <el-input-number v-model="form.pointsReward" :min="0" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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

    <el-dialog
      title="授予勋章"
      v-model="grantDialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="grantForm" :rules="grantRules" ref="grantFormRef" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="grantForm.userId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="选择勋章" prop="achievementId">
          <el-select v-model="grantForm.achievementId" placeholder="请选择勋章" style="width: 100%">
            <el-option
              v-for="item in availableAchievements"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="grantDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="grantLoading" @click="handleGrantSubmit">
          确定授予
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAchievementList,
  createAchievement,
  updateAchievement,
  deleteAchievement,
  updateAchievementStatus,
  getUserAchievementList,
  grantAchievement,
  revokeUserAchievement
} from '@/api/admin'

const activeTab = ref('achievement')
const loading = ref(false)
const userLoading = ref(false)
const submitLoading = ref(false)
const grantLoading = ref(false)
const total = ref(0)
const userTotal = ref(0)
const achievementList = ref([])
const userAchievementList = ref([])
const availableAchievements = ref([])

const searchForm = reactive({
  page: 1,
  size: 10,
  name: '',
  status: null
})

const userSearchForm = reactive({
  page: 1,
  size: 10,
  userId: null,
  achievementId: null
})

const dialogVisible = ref(false)
const grantDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const grantFormRef = ref(null)

const form = reactive({
  id: null,
  achievementCode: '',
  name: '',
  description: '',
  requirementType: 'streak',
  requirementValue: 1,
  requirementDescription: '',
  pointsReward: 0,
  sortOrder: 0,
  status: 1
})

const grantForm = reactive({
  userId: null,
  achievementId: null
})

const rules = {
  achievementCode: [
    { required: true, message: '请输入勋章代码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入勋章名称', trigger: 'blur' }
  ],
  requirementType: [
    { required: true, message: '请选择条件类型', trigger: 'change' }
  ],
  requirementValue: [
    { required: true, message: '请输入条件值', trigger: 'blur' }
  ]
}

const grantRules = {
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' }
  ],
  achievementId: [
    { required: true, message: '请选择勋章', trigger: 'change' }
  ]
}

const dialogTitle = computed(() => (isEdit.value ? '编辑勋章' : '新增勋章'))

const fetchAchievements = async () => {
  loading.value = true
  try {
    const res = await getAchievementList({
      page: searchForm.page,
      size: searchForm.size,
      name: searchForm.name,
      status: searchForm.status
    })
    if (res.code === 200) {
      achievementList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取勋章列表失败', error)
  } finally {
    loading.value = false
  }
}

const fetchUserAchievements = async () => {
  userLoading.value = true
  try {
    const params = {
      page: userSearchForm.page,
      size: userSearchForm.size
    }
    if (userSearchForm.userId) {
      params.userId = userSearchForm.userId
    }
    if (userSearchForm.achievementId) {
      params.achievementId = userSearchForm.achievementId
    }

    const res = await getUserAchievementList(params)
    if (res.code === 200) {
      userAchievementList.value = res.data.records || []
      userTotal.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取用户勋章列表失败', error)
  } finally {
    userLoading.value = false
  }
}

const fetchAvailableAchievements = async () => {
  try {
    const res = await getAchievementList({
      page: 1,
      size: 100,
      status: 1
    })
    if (res.code === 200) {
      availableAchievements.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取可用勋章失败', error)
  }
}

const handleSearch = () => {
  searchForm.page = 1
  fetchAchievements()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = null
  handleSearch()
}

const handleUserSearch = () => {
  userSearchForm.page = 1
  fetchUserAchievements()
}

const handleUserReset = () => {
  userSearchForm.userId = null
  userSearchForm.achievementId = null
  handleUserSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    achievementCode: '',
    name: '',
    description: '',
    requirementType: 'streak',
    requirementValue: 1,
    requirementDescription: '',
    pointsReward: 0,
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    achievementCode: row.achievementCode,
    name: row.name,
    description: row.description || '',
    requirementType: row.requirementType,
    requirementValue: row.requirementValue,
    requirementDescription: row.requirementDescription || '',
    pointsReward: row.pointsReward,
    sortOrder: row.sortOrder,
    status: row.status
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    let res
    if (isEdit.value) {
      res = await updateAchievement(form.id, { ...form })
    } else {
      res = await createAchievement({ ...form })
    }

    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      dialogVisible.value = false
      fetchAchievements()
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
    await ElMessageBox.confirm('确定要删除该勋章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteAchievement(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchAchievements()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

const handleStatusChange = async (row) => {
  try {
    const res = await updateAchievementStatus(row.id, row.status)
    if (res.code === 200) {
      ElMessage.success('状态更新成功')
    } else {
      row.status = row.status === 1 ? 0 : 1
      ElMessage.error(res.message || '状态更新失败')
    }
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态更新失败，请稍后重试')
  }
}

const handleGrant = () => {
  grantForm.userId = null
  grantForm.achievementId = null
  fetchAvailableAchievements()
  grantDialogVisible.value = true
}

const handleGrantSubmit = async () => {
  const valid = await grantFormRef.value.validate().catch(() => false)
  if (!valid) return

  grantLoading.value = true
  try {
    const res = await grantAchievement(grantForm.userId, grantForm.achievementId)
    if (res.code === 200) {
      ElMessage.success('授予成功')
      grantDialogVisible.value = false
      fetchUserAchievements()
    } else {
      ElMessage.error(res.message || '授予失败')
    }
  } catch (error) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    grantLoading.value = false
  }
}

const handleRevoke = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤销该用户的勋章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await revokeUserAchievement(row.id)
    if (res.code === 200) {
      ElMessage.success('撤销成功')
      fetchUserAchievements()
    } else {
      ElMessage.error(res.message || '撤销失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('撤销失败，请稍后重试')
    }
  }
}

watch(activeTab, (val) => {
  if (val === 'achievement') {
    fetchAchievements()
  } else {
    fetchUserAchievements()
  }
})

onMounted(() => {
  fetchAchievements()
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
  font-weight: bold;
}
</style>
