<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="处理状态" clearable style="width: 130px" @change="load">
          <el-option label="待处理" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已完成" :value="2" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <div class="spacer" />
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>&nbsp;新增报修
        </el-button>
      </div>

      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column label="房间" width="120">
          <template #default="{ row }">{{ row.buildingNo ? row.buildingNo + ' ' : '' }}{{ row.roomNo || '—' }}</template>
        </el-table-column>
        <el-table-column prop="reporter" label="报修人" width="90" />
        <el-table-column prop="phone" label="联系电话" width="125" />
        <el-table-column prop="content" label="报修内容" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="165" />
        <el-table-column prop="finishTime" label="完成时间" width="165">
          <template #default="{ row }">{{ row.finishTime || '—' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="primary" @click="changeStatus(row, 1)">开始处理</el-button>
            <el-button v-if="row.status === 1" link type="success" @click="changeStatus(row, 2)">完成</el-button>
            <el-popconfirm title="确定删除该报修记录吗？" @confirm="remove(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" title="新增报修" width="480px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="房间" required>
          <el-select v-model="form.roomId" placeholder="请选择房间" filterable style="width: 100%">
            <el-option v-for="r in rooms" :key="r.id" :label="`${r.buildingNo} ${r.roomNo}`" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修人">
          <el-input v-model="form.reporter" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="报修内容" required>
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请描述故障情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">确 定</el-button>
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
const rooms = ref([])
const dialogVisible = ref(false)

const query = reactive({ page: 1, size: 10, status: null })
const form = reactive({ roomId: null, reporter: '', phone: '', content: '' })

const statusText = (s) => (s === 0 ? '待处理' : s === 1 ? '处理中' : '已完成')
const statusType = (s) => (s === 0 ? 'danger' : s === 1 ? 'warning' : 'success')

const load = async () => {
  loading.value = true
  try {
    const data = await request.get('/repairs', { params: query })
    rows.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const loadRooms = async () => {
  rooms.value = await request.get('/rooms/all')
}

const openDialog = () => {
  form.roomId = null
  form.reporter = ''
  form.phone = ''
  form.content = ''
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.roomId || !form.content) {
    ElMessage.warning('请选择房间并填写报修内容')
    return
  }
  saving.value = true
  try {
    await request.post('/repairs', form)
    ElMessage.success('报修提交成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const changeStatus = async (row, status) => {
  await request.put('/repairs', { id: row.id, status })
  ElMessage.success(statusText(status) + '成功')
  load()
}

const remove = async (row) => {
  await request.delete(`/repairs/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(() => {
  loadRooms()
  load()
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
