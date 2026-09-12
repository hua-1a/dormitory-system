<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <el-select v-model="query.type" placeholder="记录类型" clearable style="width: 130px" @change="load">
          <el-option label="入住" value="IN" />
          <el-option label="退宿" value="OUT" />
        </el-select>
        <div class="spacer" />
        <el-button type="success" @click="openCheckInDialog">
          <el-icon><Plus /></el-icon>&nbsp;办理入住
        </el-button>
        <el-button type="warning" @click="openCheckOutDialog">
          <el-icon><Minus /></el-icon>&nbsp;办理退宿
        </el-button>
      </div>

      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column label="宿舍" width="120">
          <template #default="{ row }">{{ row.buildingNo }}{{ row.roomNo }}</template>
        </el-table-column>
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.type === 'IN' ? 'success' : 'danger'">
              {{ row.type === 'IN' ? '入住' : '退宿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="时间" min-width="170" />
        <el-table-column prop="operator" label="操作人" width="110" />
      </el-table>

      <el-pagination
        class="pager"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :page-sizes="[10, 20, 50]"
        @change="load"
      />
    </el-card>

    <!-- 办理入住 -->
    <el-dialog v-model="checkInVisible" title="办理入住" width="480px" destroy-on-close>
      <el-form label-width="90px">
        <el-form-item label="学生" required>
          <el-select v-model="checkInForm.studentId" placeholder="选择未入住的学生" filterable style="width: 100%">
            <el-option v-for="s in unhousedStudents" :key="s.id" :label="`${s.name}（${s.studentNo}）`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间" required>
          <el-select v-model="checkInForm.roomId" placeholder="选择有空床位的房间" filterable style="width: 100%">
            <el-option
              v-for="r in availableRooms"
              :key="r.id"
              :label="`${r.buildingNo} ${r.roomNo}（已住 ${r.usedCount}/${r.bedCount}）`"
              :value="r.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkInVisible = false">取 消</el-button>
        <el-button type="primary" :loading="saving" @click="submitCheckIn">确 定</el-button>
      </template>
    </el-dialog>

    <!-- 办理退宿 -->
    <el-dialog v-model="checkOutVisible" title="办理退宿" width="480px" destroy-on-close>
      <el-form label-width="90px">
        <el-form-item label="学生" required>
          <el-select v-model="checkOutForm.studentId" placeholder="选择已入住的学生" filterable style="width: 100%">
            <el-option v-for="s in housedStudents" :key="s.id" :label="`${s.name}（${s.studentNo}）`" :value="s.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="checkOutVisible = false">取 消</el-button>
        <el-button type="primary" :loading="saving" @click="submitCheckOut">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const unhousedStudents = ref([])
const housedStudents = ref([])
const availableRooms = ref([])

const checkInVisible = ref(false)
const checkOutVisible = ref(false)
const checkInForm = reactive({ studentId: null, roomId: null })
const checkOutForm = reactive({ studentId: null })

const query = reactive({ page: 1, size: 10, type: '' })

const user = JSON.parse(localStorage.getItem('dorm_user') || 'null')

const load = async () => {
  loading.value = true
  try {
    const data = await request.get('/checkins', { params: query })
    rows.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const loadOptions = async () => {
  const [unhoused, housed, rooms] = await Promise.all([
    request.get('/students/options', { params: { status: 0 } }),
    request.get('/students/options', { params: { status: 1 } }),
    request.get('/rooms/options')
  ])
  unhousedStudents.value = unhoused
  housedStudents.value = housed
  availableRooms.value = rooms
}

const openCheckInDialog = () => {
  checkInForm.studentId = null
  checkInForm.roomId = null
  checkInVisible.value = true
}

const openCheckOutDialog = () => {
  checkOutForm.studentId = null
  checkOutVisible.value = true
}

const submitCheckIn = async () => {
  if (!checkInForm.studentId || !checkInForm.roomId) {
    ElMessage.warning('请选择学生和房间')
    return
  }
  saving.value = true
  try {
    await request.post('/checkins', { ...checkInForm, operator: user?.realName || 'admin' })
    ElMessage.success('办理入住成功')
    checkInVisible.value = false
    load()
    loadOptions()
  } finally {
    saving.value = false
  }
}

const submitCheckOut = async () => {
  if (!checkOutForm.studentId) {
    ElMessage.warning('请选择学生')
    return
  }
  saving.value = true
  try {
    await request.post('/checkins/checkout', { ...checkOutForm, operator: user?.realName || 'admin' })
    ElMessage.success('办理退宿成功')
    checkOutVisible.value = false
    load()
    loadOptions()
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  load()
  loadOptions()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
}

.spacer {
  flex: 1;
}

.pager {
  margin-top: 14px;
  justify-content: flex-end;
}

.panel :deep(.el-card__body) {
  padding: 16px;
}
</style>
