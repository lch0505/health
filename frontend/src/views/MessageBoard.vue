<template>
  <div class="page-container">
    <el-card class="message-board-card">
      <template #header>
        <div class="flex-between">
          <span>留言板</span>
          <el-button type="primary" @click="handleAddMessage">
            <el-icon><Edit /></el-icon>
            发布留言
          </el-button>
        </div>
      </template>

      <div class="message-list" v-loading="loading">
        <div
          v-for="item in messageList"
          :key="item.id"
          class="message-item"
        >
          <div class="message-header">
            <div class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="nickname">{{ item.nickname }}</span>
            </div>
            <div class="message-actions" v-if="canEdit(item)">
              <el-button type="primary" link size="small" @click="handleEditMessage(item)">
                编辑
              </el-button>
              <el-button type="danger" link size="small" @click="handleDeleteMessage(item)">
                删除
              </el-button>
            </div>
          </div>
          <div class="message-content">{{ item.content }}</div>
          <div class="message-time">{{ formatTime(item.createTime) }}</div>
        </div>

        <el-empty v-if="messageList.length === 0 && !loading" description="暂无留言，快来发布第一条留言吧！" />
      </div>

      <div class="pagination mt-20">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <el-dialog 
      :title="isEdit ? '编辑留言' : '发布留言'" 
      v-model="dialogVisible" 
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="留言内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="5"
            placeholder="请输入留言内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMessageBoardList, createMessageBoard, updateMessageBoard, deleteMessageBoard } from '@/api/messageBoard'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const messageList = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  content: ''
})

const rules = {
  content: [
    { required: true, message: '请输入留言内容', trigger: 'blur' },
    { min: 1, max: 500, message: '留言内容长度在 1 到 500 个字符', trigger: 'blur' }
  ]
}

const currentUserId = computed(() => userStore.userInfo?.userId)
const isAdmin = computed(() => userStore.isAdmin)

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

const canEdit = (item) => {
  return item.userId === currentUserId.value || isAdmin.value
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size
    }

    const res = await getMessageBoardList(params)
    if (res.code === 200) {
      messageList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取留言列表失败', error)
    ElMessage.error('获取留言列表失败')
  } finally {
    loading.value = false
  }
}

const handleAddMessage = () => {
  isEdit.value = false
  form.id = null
  form.content = ''
  dialogVisible.value = true
}

const handleEditMessage = (item) => {
  isEdit.value = true
  form.id = item.id
  form.content = item.content
  dialogVisible.value = true
}

const handleDeleteMessage = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除这条留言吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await deleteMessageBoard(item.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除留言失败', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        let res
        if (isEdit.value) {
          res = await updateMessageBoard({
            id: form.id,
            content: form.content
          })
        } else {
          res = await createMessageBoard({
            content: form.content
          })
        }

        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
          dialogVisible.value = false
          fetchList()
        } else {
          ElMessage.error(res.message || (isEdit.value ? '更新失败' : '发布失败'))
        }
      } catch (error) {
        console.error(isEdit.value ? '更新留言失败' : '发布留言失败', error)
        ElMessage.error(isEdit.value ? '更新失败' : '发布失败')
      } finally {
        submitting.value = false
      }
    }
  })
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

.mt-20 {
  margin-top: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.message-list {
  .message-item {
    padding: 20px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 20px;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    .message-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .user-info {
        display: flex;
        align-items: center;

        .nickname {
          margin-left: 10px;
          font-weight: 500;
          color: #303133;
        }
      }

      .message-actions {
        display: flex;
        gap: 5px;
      }
    }

    .message-content {
      font-size: 15px;
      color: #303133;
      line-height: 1.8;
      margin-bottom: 10px;
      white-space: pre-wrap;
      word-break: break-word;
    }

    .message-time {
      font-size: 13px;
      color: #909399;
    }
  }
}
</style>
