<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <div class="toolbar">
        <el-input
          v-model="keyword"
          placeholder="楼栋编号"
          clearable
          style="width: 200px"
          @keyup.enter="load"
          @clear="load"
        />
        <el-button type="primary" @click="load">查询</el-button>
        <div class="spacer" />
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>&nbsp;新增楼栋
        </el-button>
      </div>

      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column prop="buildingNo" label="楼栋编号" width="110" />
        <el-table-column prop="floorCount" label="层数" width="70" align="center" />
        <el-table-column prop="roomCount" label="房间数" width="80" align="center" />
        <el-table-column prop="bedCount" label="总床位" width="90" align="center" />
        <el-table-column prop="usedBedCount" label="已入住" width="90" align="center" />
        <el-table-column label="入住率" width="170">
          <template #default="{ row }">
            <el-progress :percentage="rate(row)" :color="rateColor(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="manager" label="宿管员" width="90" />
        <el-table-column prop="phone" label="联系电话" width="125" />
        <el-table-column prop="remark" label="备注" min-width="110" show-overflow-tooltip />
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该楼栋吗？" @confirm="remove(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑楼栋' : '新增楼栋'" width="480px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="楼栋编号" required>
          <el-input v-model="form.buildingNo" placeholder="如 D栋" />
        </el-form-item>
        <el-form-item label="层数">
          <el-input-number v-model="form.floorCount" :min="1" :max="30" />
        </el-form-item>
        <el-form-item label="宿管员">
          <el-input v-model="form.manager" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="如：男生宿舍 / 女生宿舍" />
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
const dialogVisible = ref(false)
const keyword = ref('')

const emptyForm = () => ({ id: null, buildingNo: '', floorCount: 6, manager: '', phone: '', remark: '' })
const form = reactive(emptyForm())

const load = async () => {
  loading.value = true
  try {
    rows.value = await request.get('/dormitories', { params: { keyword: keyword.value } })
  } finally {
    loading.value = false
  }
}

const rate = (row) => {
  if (!row.bedCount) return 0
  return Math.round((row.usedBedCount / row.bedCount) * 100)
}

const rateColor = (row) => {
  const r = rate(row)
  if (r >= 90) return '#f56c6c'
  if (r >= 60) return '#e6a23c'
  return '#67c23a'
}

const openDialog = (row) => {
  Object.assign(form, row ? { ...row } : emptyForm())
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.buildingNo) {
    ElMessage.warning('请输入楼栋编号')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await request.put('/dormitories', form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/dormitories', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const remove = async (row) => {
  await request.delete(`/dormitories/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
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

.panel :deep(.el-card__body) {
  padding: 16px;
}
</style>
