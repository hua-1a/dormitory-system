<template>
  <div class="page">
    <el-card shadow="never" class="panel">
      <!-- 工具栏 -->
      <div class="toolbar">
        <el-input
          v-model="query.keyword"
          placeholder="学号 / 姓名"
          clearable
          style="width: 200px"
          @keyup.enter="load"
          @clear="load"
        />
        <el-select v-model="query.gender" placeholder="性别" clearable style="width: 110px" @change="load">
          <el-option label="男" value="男" />
          <el-option label="女" value="女" />
        </el-select>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <div class="spacer" />
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>&nbsp;新增学生
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="rows" v-loading="loading" stripe>
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="90" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="college" label="学院" min-width="120" show-overflow-tooltip />
        <el-table-column prop="major" label="专业" min-width="150" show-overflow-tooltip />
        <el-table-column prop="grade" label="年级" width="80" />
        <el-table-column prop="phone" label="手机号" width="125" />
        <el-table-column label="宿舍" width="100">
          <template #default="{ row }">{{ row.roomNo || '未入住' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已入住' : '未入住' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除该学生吗？" @confirm="remove(row)">
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑学生' : '新增学生'" width="540px" destroy-on-close>
      <el-form :model="form" label-width="70px">
        <el-form-item label="学号" required>
          <el-input v-model="form.studentNo" placeholder="如 20230501" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="form.college" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="form.grade" placeholder="如 2023级" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
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
const dialogVisible = ref(false)

const query = reactive({ page: 1, size: 10, keyword: '', gender: '' })

const emptyForm = () => ({ id: null, studentNo: '', name: '', gender: '男', college: '', major: '', grade: '', phone: '' })
const form = reactive(emptyForm())

const load = async () => {
  loading.value = true
  try {
    const data = await request.get('/students', { params: query })
    rows.value = data.records
    total.value = Number(data.total)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.keyword = ''
  query.gender = ''
  query.page = 1
  load()
}

const openDialog = (row) => {
  Object.assign(form, row ? { ...row } : emptyForm())
  dialogVisible.value = true
}

const submit = async () => {
  if (!form.studentNo || !form.name) {
    ElMessage.warning('学号和姓名为必填项')
    return
  }
  saving.value = true
  try {
    if (form.id) {
      await request.put('/students', form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/students', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

const remove = async (row) => {
  await request.delete(`/students/${row.id}`)
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
  flex-wrap: wrap;
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
