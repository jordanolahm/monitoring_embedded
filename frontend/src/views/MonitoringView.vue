<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../services/api'

type Camera = {
  id: number
  name: string
  ip: string
  active: boolean
}

type MonitoringSummary = {
  status?: string
  responseTimeMs?: number | null
  usagePercentage?: number | null
  totalBytes?: number | null
  usedBytes?: number | null
  freeBytes?: number | null
  executedAt?: string
  errorMessage?: string | null
}

const internet = ref<MonitoringSummary | null>(null)
const disk = ref<MonitoringSummary | null>(null)
const cameras = ref<Camera[]>([])
const cameraStatuses = ref<Record<number, MonitoringSummary | null>>({})

async function loadInternet() {
  const response = await api.get('/monitoring/internet/latest')
  internet.value = response.data.data ?? null
}

async function loadDisk() {
  const response = await api.get('/monitoring/disk/latest')
  disk.value = response.data.data ?? null
}

async function loadCameras() {
  const response = await api.get('/cameras')
  cameras.value = response.data.data ?? []

  for (const camera of cameras.value) {
    const status = await api.get(`/monitoring/cameras/${camera.id}/latest`)
    cameraStatuses.value[camera.id] = status.data.data ?? null
  }
}

function formatBytes(value?: number | null) {
  if (!value) return '0 MB'
  return `${(value / 1024 / 1024).toFixed(2)} MB`
}

function formatDate(value?: string | null) {
  if (!value) return '-'
  return new Date(value).toLocaleString('pt-BR')
}

onMounted(() => {
  loadInternet()
  loadDisk()
  loadCameras()
})
</script>

<template>
  <div class="monitoring-page">
    <header class="topbar">
      <div>
        <h2>Monitoramento</h2>
        <nav class="nav">
          <router-link to="/dashboard">Resumo</router-link>
          <router-link to="/cameras">Câmeras</router-link>
          <router-link to="/alerts">Alertas</router-link>
          <router-link to="/monitoring">Monitoramento</router-link>
          <router-link to="/settings">Configurações</router-link>
        </nav>
      </div>
    </header>

    <section class="grid">
      <article class="panel">
        <h3>Internet</h3>
        <p><strong>Status:</strong> {{ internet?.status ?? 'SEM DADOS' }}</p>
        <p><strong>Tempo de resposta:</strong> {{ internet?.responseTimeMs ?? 0 }} ms</p>
        <p><strong>Última execução:</strong> {{ internet?.executedAt ? new Date(internet.executedAt).toLocaleString('pt-BR') : '-' }}</p>
        <p v-if="internet?.errorMessage"><strong>Erro:</strong> {{ internet.errorMessage }}</p>
      </article>

      <article class="panel">
        <h3>Disco</h3>
        <p><strong>Uso:</strong> {{ disk?.usagePercentage ? Number(disk.usagePercentage).toFixed(2) : '0.00' }}%</p>
        <p><strong>Usado:</strong> {{ formatBytes(disk?.usedBytes) }}</p>
        <p><strong>Disponível:</strong> {{ formatBytes(disk?.freeBytes) }}</p>
        <p><strong>Executado em:</strong> {{ disk?.executedAt ? new Date(disk.executedAt).toLocaleString('pt-BR') : '-' }}</p>
      </article>
    </section>

    <section class="panel">
      <h3>Câmeras</h3>
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>IP</th>
            <th>Status</th>
            <th>Resposta</th>
            <th>Última execução</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="camera in cameras" :key="camera.id">
            <td>{{ camera.name }}</td>
            <td>{{ camera.ip }}</td>
            <td>{{ cameraStatuses[camera.id]?.status ?? 'SEM DADOS' }}</td>
            <td>{{ cameraStatuses[camera.id]?.responseTimeMs ?? '-' }} ms</td>
            <td>{{ formatDate(cameraStatuses[camera.id]?.executedAt) }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<style scoped>
.monitoring-page {
  padding: 2rem;
  background: #e5e7eb;
  min-height: 100vh;
  color: #1f2937;
}

.topbar {
  margin-bottom: 1.5rem;
}

.nav {
  display: flex;
  gap: 1rem;
  margin-top: 0.75rem;
  flex-wrap: wrap;
}

.nav a {
  color: #4b5563;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.panel {
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(107, 114, 128, 0.2);
  border-radius: 12px;
  padding: 1rem;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

th, td {
  border: 1px solid rgba(107, 114, 128, 0.25);
  padding: 0.85rem;
  text-align: left;
}
</style>
