<template>
  <div class="dashboard">
    <!-- 欢迎语 -->
    <div class="welcome">
      <div>
        <h2 class="welcome-title">欢迎回来，{{ user?.realName || '管理员' }} 👋</h2>
        <p class="welcome-sub">今天是 {{ today }}，这里是宿舍管理整体情况一览</p>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="cards">
      <div v-for="card in cards" :key="card.label" class="stat-card" :style="{ background: card.bg }">
        <div class="stat-icon">
          <el-icon :size="30"><component :is="card.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </div>
      </div>
    </div>

    <!-- 第一行图表 -->
    <el-row :gutter="16" class="row">
      <el-col :xs="24" :md="16">
        <el-card shadow="never" class="panel">
          <template #header><span class="panel-title">各楼栋入住情况</span></template>
          <div ref="buildingRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel">
          <template #header><span class="panel-title">学生性别分布</span></template>
          <div ref="genderRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="16" class="row">
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel">
          <template #header><span class="panel-title">报修处理状态</span></template>
          <div ref="repairRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="16">
        <el-card shadow="never" class="panel">
          <template #header><span class="panel-title">近期入住动态</span></template>
          <el-table :data="recentCheckIns" size="small" empty-text="暂无记录">
            <el-table-column prop="studentName" label="学生" width="90" />
            <el-table-column prop="studentNo" label="学号" width="120" />
            <el-table-column label="宿舍" width="110">
              <template #default="{ row }">{{ row.buildingNo }}{{ row.roomNo }}</template>
            </el-table-column>
            <el-table-column label="类型" width="80">
              <template #default="{ row }">
                <el-tag :type="row.type === 'IN' ? 'success' : 'danger'" size="small">
                  {{ row.type === 'IN' ? '入住' : '退宿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="时间" />
            <el-table-column prop="operator" label="操作人" width="90" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import * as echarts from 'echarts'
import request from '../utils/request'
import { User, OfficeBuilding, House, Platform, DataLine } from '@element-plus/icons-vue'

const user = ref(JSON.parse(localStorage.getItem('dorm_user') || 'null'))

const today = new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
})

const stats = reactive({
  totalStudents: 0,
  totalDorms: 0,
  totalRooms: 0,
  totalBeds: 0,
  usedBeds: 0,
  occupancyRate: 0,
  genderDist: [],
  gradeDist: [],
  buildingStats: [],
  repairStats: [],
  recentCheckIns: []
})

const cards = computed(() => [
  { label: '学生总数', value: stats.totalStudents, icon: User, bg: 'linear-gradient(135deg, #409eff, #53a8ff)' },
  { label: '宿舍楼栋', value: stats.totalDorms, icon: OfficeBuilding, bg: 'linear-gradient(135deg, #67c23a, #85ce61)' },
  { label: '房间总数', value: stats.totalRooms, icon: House, bg: 'linear-gradient(135deg, #e6a23c, #ebb563)' },
  { label: '总床位', value: stats.totalBeds, icon: Platform, bg: 'linear-gradient(135deg, #9c27b0, #b37feb)' },
  { label: '整体入住率', value: stats.occupancyRate + '%', icon: DataLine, bg: 'linear-gradient(135deg, #f56c6c, #f78989)' }
])

const buildingRef = ref()
const genderRef = ref()
const repairRef = ref()
let buildingChart = null
let genderChart = null
let repairChart = null

const recentCheckIns = computed(() => stats.recentCheckIns)

const loadStats = async () => {
  const data = await request.get('/dashboard/stats')
  Object.assign(stats, data)
  renderCharts()
}

const renderCharts = () => {
  renderBuildingChart()
  renderGenderChart()
  renderRepairChart()
}

const renderBuildingChart = () => {
  if (!buildingRef.value) return
  buildingChart = buildingChart || echarts.init(buildingRef.value)
  const names = stats.buildingStats.map((b) => b.name)
  buildingChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['总床位', '已入住'] },
    grid: { left: 40, right: 20, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: names },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      {
        name: '总床位',
        type: 'bar',
        barWidth: 22,
        itemStyle: { color: '#c0c4cc', borderRadius: [4, 4, 0, 0] },
        data: stats.buildingStats.map((b) => b.bed_count)
      },
      {
        name: '已入住',
        type: 'bar',
        barWidth: 22,
        itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] },
        data: stats.buildingStats.map((b) => b.used_bed_count)
      }
    ]
  })
}

const renderGenderChart = () => {
  if (!genderRef.value) return
  genderChart = genderChart || echarts.init(genderRef.value)
  genderChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}：{c} 人（{d}%）' },
    legend: { bottom: 0 },
    color: ['#409eff', '#f56c6c'],
    series: [
      {
        type: 'pie',
        radius: ['42%', '66%'],
        center: ['50%', '45%'],
        label: { formatter: '{b}\n{c}人' },
        data: stats.genderDist
      }
    ]
  })
}

const renderRepairChart = () => {
  if (!repairRef.value) return
  repairChart = repairChart || echarts.init(repairRef.value)
  repairChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}：{c} 条（{d}%）' },
    legend: { bottom: 0 },
    color: ['#f56c6c', '#e6a23c', '#67c23a'],
    series: [
      {
        type: 'pie',
        radius: ['42%', '66%'],
        center: ['50%', '45%'],
        label: { formatter: '{b}\n{c}条' },
        data: stats.repairStats
      }
    ]
  })
}

const handleResize = () => {
  buildingChart && buildingChart.resize()
  genderChart && genderChart.resize()
  repairChart && repairChart.resize()
}

onMounted(() => {
  loadStats()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  buildingChart && buildingChart.dispose()
  genderChart && genderChart.dispose()
  repairChart && repairChart.dispose()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.welcome-title {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.welcome-sub {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

@media (max-width: 1200px) {
  .cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

.stat-card {
  border-radius: 12px;
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  color: #fff;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.12);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.22);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  margin-top: 2px;
  font-size: 13px;
  opacity: 0.92;
}

.row {
  margin: 0 !important;
}

.panel {
  border-radius: 12px;
}

.panel :deep(.el-card__header) {
  padding: 14px 18px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.chart {
  height: 300px;
}
</style>
