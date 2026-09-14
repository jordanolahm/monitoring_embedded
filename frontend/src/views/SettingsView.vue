<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../services/api'

const config = ref({
  internetIpTest: '8.8.8.8',
  internetTimeoutMs: 5000,
  internetPeriodSeconds: 60,
  diskPeriodSeconds: 120,
  diskAlertThresholdPercent: 85,
  cameraPeriodSeconds: 180
})

async function loadConfig() {
  const response = await api.get('/configurations')
  if (response.data?.data) {
    config.value = response.data.data
  }
}

async function saveConfig() {
  await api.put('/configurations', config.value)
  alert('Configuração salva com sucesso.')
}

onMounted(() => {
  loadConfig()
})
</script>

<template>
  <div class="settings-page">
    <div class="settings-header">
      <h2>Configurações gerais</h2>
      <router-link to="/dashboard" class="back-button">Voltar ao dashboard</router-link>
    </div>

    <div class="grid">
      <label>
        IP para teste de internet
        <input v-model="config.internetIpTest" type="text" />
      </label>

      <label>
        Timeout (ms)
        <input v-model.number="config.internetTimeoutMs" type="number" />
      </label>

      <label>
        Periodicidade internet (s)
        <input v-model.number="config.internetPeriodSeconds" type="number" />
      </label>

      <label>
        Periodicidade disco (s)
        <input v-model.number="config.diskPeriodSeconds" type="number" />
      </label>

      <label>
        Limite alerta disco (%)
        <input v-model.number="config.diskAlertThresholdPercent" type="number" />
      </label>

      <label>
        Periodicidade câmeras (s)
        <input v-model.number="config.cameraPeriodSeconds" type="number" />
      </label>
    </div>

    <button @click="saveConfig">Salvar configuração</button>
  </div>
</template>

<style scoped>
.settings-page {
  padding: 2rem;
  color: #1f2937;
  background: #e5e7eb;
  min-height: 100vh;
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.back-button {
  background: #6b7280;
  color: white;
  border-radius: 10px;
  padding: 0.75rem 1rem;
  font-weight: 600;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1rem;
  margin: 1.5rem 0;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

input {
  border-radius: 10px;
  border: 1px solid #9ca3af;
  background: white;
  color: #1f2937;
  padding: 0.75rem;
}

button {
  background: #22c55e;
  color: #062b12;
  border: none;
  border-radius: 10px;
  padding: 0.85rem 1.25rem;
  font-weight: 700;
  cursor: pointer;
}
</style>
