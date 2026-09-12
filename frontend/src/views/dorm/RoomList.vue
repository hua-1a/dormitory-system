<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <el-select v-model="query.dormitoryId" placeholder="楼栋" clearable style="width: 140px" @change="load">
          <el-option v-for="d in dormitories" :key="d.id" :label="d.buildingNo" :value="d.id" />
        </el-select>
        <el-input
          v-model="query.keyword"
          placeholder="房间号"
          clearable
          style="width: 160px"
          @keyup.enter="load"
          @clear="load"
        />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 110px" @change="load">
          <el-option label="可用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <div class="spacer" />
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>&nbsp;新增房间
        </el-button>
      </div>

      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column prop="roomNo" label="房间号" width="110" />
        <el-table-column prop="buildingNo" label="楼栋" width="90" />
        <el-table-column prop="bedCount" label="床位数" width="90" align="center" />
        <el-table-column prop="usedCount" label="已入住" width="90" align="center" />
        <el-table-column label="剩余床位" width="90" align="center">
          <template #default="{ row }">
            <span :class="{ full: row.usedCount >= row.bedCount }">{{ row.bedCount - row.usedCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '可用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-popconfirm title="确定删除该房间吗？" @confirm="remove(row)">
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

    <el-dialog v-model="dialogVisible" title="新增房间" width="420px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="所属楼栋" required>
          <el-select v-model="form.dormitoryId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="d in dormitories" :key="d.id" :label="d.buildingNo" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" required>
          <el-input v-model="form.roomNo" placeholder="如 D-101" />
        </el-form-item>
        <el-form-item label="床位数">
          <el-input-number v-model="form.bedCount" :min="1" :max="8" />
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
const dormitories = ref([])
const dialogVisible = ref(false)

const query = reactive({ page: 1, size: 10, dormitoryId: null, keyword: '', status: null })
const form = reactive({ dormitoryId: null, roomNo: '', bedCount: 4 })

const load = async () => {
  loading.value = true
  try {
    const data = await request.get('/rooms', { params: query })
    rows.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const loadDormitories = async () => {
  dormitories.value = await request.get('/dormitories')
}

const openDialog = () => {
  form.dormitoryId = null
  form.roomNo = ''
  form.bedCount = 4
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.dormitoryId || !form.roomNo) {
    ElMessage.warning('请选择楼栋并填写房间号')
    return
  }
  saving.value = true
  try {
    await request.post('/rooms', form)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (row) => {
  await request.put('/rooms', { id: row.id, status: row.status === 1 ? 0 : 1 })
  ElMessage.success(row.status === 1 ? '已停用' : '已启用')
  load()
}

const remove = async (row) => {
  await request.delete(`/rooms/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(() => {
  loadDormitories()
  load()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 14px;
  flex-wrap: wrap;
}

.spacer {
  flex: 1;
}

.pager {
  margin-top: 14px;
  justify-content: flex-end;
}

.full {
  color: #f56c6c;
  font-weight: 600;
}

.panel :deep(.el-card__body) {
  padding: 16px;
}
</style>
