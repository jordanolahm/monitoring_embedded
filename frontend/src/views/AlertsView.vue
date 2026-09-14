<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../services/api'

type AlertItem = {
  id?: number
  type: string
  severity: string
  status: string
  message: string
  createdAt: string
}

const alerts = ref<AlertItem[]>([])

async function loadAlerts() {
  const response = await api.get('/alerts')
  alerts.value = response.data.data ?? []
}

function formatDate(value?: string) {
  if (!value) return '-'
  return new Date(value).toLocaleString('pt-BR')
}

onMounted(() => {
  loadAlerts()
})
</script>

<template>
  <div class="alerts-page">
    <header class="topbar">
      <div>
        <h2>Alertas</h2>
        <nav class="nav">
          <router-link to="/dashboard">Resumo</router-link>
          <router-link to="/cameras">Câmeras</router-link>
          <router-link to="/alerts">Alertas</router-link>
          <router-link to="/settings">Configurações</router-link>
        </nav>
      </div>
      <router-link to="/dashboard" class="back-button">Voltar ao dashboard</router-link>
    </header>

    <section class="panel">
      <h3>Histórico de alertas</h3>

      <table>
        <thead>
          <tr>
            <th>Tipo</th>
            <th>Severidade</th>
            <th>Status</th>
            <th>Mensagem</th>
            <th>Data</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="alert in alerts" :key="alert.id">
            <td>{{ alert.type }}</td>
            <td>{{ alert.severity }}</td>
            <td>{{ alert.status }}</td>
            <td>{{ alert.message }}</td>
            <td>{{ formatDate(alert.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<style scoped>
.alerts-page {
  padding: 2rem;
  background: #e5e7eb;
  min-height: 100vh;
  color: #1f2937;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.back-button {
  background: #6b7280;
  color: white;
  border-radius: 10px;
  padding: 0.75rem 1rem;
  font-weight: 600;
}

.nav {
  display: flex;
  gap: 1rem;
  margin-top: 0.75rem;
}

.nav a {
  color: #4b5563;
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
  vertical-align: top;
}
</style>
