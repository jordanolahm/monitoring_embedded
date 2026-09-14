<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const router = useRouter()
const summary = ref<any>(null)
const alerts = ref<any[]>([])

async function loadDashboard() {
  try {
    const response = await api.get('/dashboard/summary')
    summary.value = response.data.data
    alerts.value = response.data.data?.recentAlerts ?? []
  } catch (error) {
    console.error('Erro ao carregar dashboard:', error)
  }
}

function logout() {
  localStorage.removeItem('monitoramento-auth')
  localStorage.removeItem('monitoramento-token')
  router.push('/login')
}

onMounted(() => {
  loadDashboard()
})
</script>

<template>
  <div class="dashboard">
    <header class="topbar">
      <div>
        <h2>Dashboard</h2>
        <nav class="nav">
          <router-link to="/dashboard">Resumo</router-link>
          <router-link to="/cameras">Câmeras</router-link>
          <router-link to="/alerts">Alertas</router-link>
          <router-link to="/monitoring">Monitoramento</router-link>
          <router-link to="/settings">Configurações</router-link>
        </nav>
      </div>
      <button class="logout" @click="logout">Sair</button>
    </header>

    <section class="cards" v-if="summary">
      <div class="card">
        <span>Status da Internet</span>
        <strong>{{ summary.internetStatus }}</strong>
      </div>
      <div class="card">
        <span>Câmeras Ativas</span>
        <strong>{{ summary.activeCameras }}</strong>
      </div>
      <div class="card">
        <span>Total de Câmeras</span>
        <strong>{{ summary.totalCameras }}</strong>
      </div>
      <div class="card">
        <span>Alertas</span>
        <strong>{{ alerts.length }}</strong>
      </div>
    </section>

    <section class="panel" v-if="summary?.diskLatest && Object.keys(summary.diskLatest).length">
      <h3>Monitoramento do disco</h3>
      <p>Total: {{ (summary.diskLatest.totalBytes / 1024 / 1024 / 1024).toFixed(2) }} GB</p>
      <p>Usado: {{ (summary.diskLatest.usedBytes / 1024 / 1024 / 1024).toFixed(2) }} GB</p>
      <p>Disponível: {{ (summary.diskLatest.freeBytes / 1024 / 1024 / 1024).toFixed(2) }} GB</p>
      <p>Percentual: {{ Number(summary.diskLatest.usagePercentage).toFixed(2) }}%</p>
    </section>

    <section class="panel">
      <h3>Alertas recentes</h3>
      <ul v-if="alerts.length">
        <li v-for="alert in alerts" :key="alert.id">
          {{ alert.message }} - {{ alert.status }}
        </li>
      </ul>
      <p v-else>Nenhum alerta recente.</p>
    </section>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 2rem;
  background: #e5e7eb;
  min-height: 100vh;
  color: #1f2937;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.nav {
  display: flex;
  gap: 1rem;
  margin-top: 0.75rem;
}

.nav a {
  color: #4b5563;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem;
}

.card,
.panel {
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(107, 114, 128, 0.2);
  border-radius: 12px;
  padding: 1rem;
}

.card {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.logout {
  background: #475569;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1rem;
  cursor: pointer;
}

.panel {
  margin-top: 1.5rem;
}

ul {
  list-style: none;
  padding-left: 0;
}

li {
  margin-bottom: 0.5rem;
}
</style>
