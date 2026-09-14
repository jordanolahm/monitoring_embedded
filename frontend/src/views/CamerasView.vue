<script setup lang="ts">
import { ref, onMounted } from 'vue'
import api from '../services/api'

type Camera = {
  id?: number
  name: string
  description: string
  ip: string
  httpPort: number
  rtspPort: number
  username: string
  password?: string
  active: boolean
}

const cameras = ref<Camera[]>([])
const editingId = ref<number | null>(null)
const form = ref<Camera>({
  name: '',
  description: '',
  ip: '',
  httpPort: 80,
  rtspPort: 554,
  username: '',
  password: '',
  active: true
})

function emptyForm(): Camera {
  return {
    name: '',
    description: '',
    ip: '',
    httpPort: 80,
    rtspPort: 554,
    username: '',
    password: '',
    active: true
  }
}

function resetForm() {
  editingId.value = null
  form.value = emptyForm()
}

async function loadCameras() {
  const response = await api.get('/cameras')
  cameras.value = response.data.data ?? []
}

async function saveCamera() {
  const payload = {
    ...form.value,
    password: form.value.password?.trim() ? form.value.password : undefined
  }

  if (editingId.value) {
    await api.put(`/cameras/${editingId.value}`, payload)
  } else {
    await api.post('/cameras', payload)
  }

  resetForm()
  await loadCameras()
}

function editCamera(camera: Camera) {
  editingId.value = camera.id ?? null
  form.value = {
    ...camera,
    password: ''
  }
}

async function deleteCamera(id?: number) {
  if (!id) return
  if (!window.confirm('Deseja remover esta câmera?')) return

  await api.delete(`/cameras/${id}`)
  await loadCameras()

  if (editingId.value === id) {
    resetForm()
  }
}

onMounted(() => {
  loadCameras()
})
</script>

<template>
  <div class="camera-page">
    <header class="topbar">
      <div>
        <h2>Câmeras</h2>
        <nav class="nav">
          <router-link to="/dashboard">Resumo</router-link>
          <router-link to="/cameras">Câmeras</router-link>
          <router-link to="/alerts">Alertas</router-link>
          <router-link to="/settings">Configurações</router-link>
        </nav>
      </div>
    </header>

    <section class="panel form-panel">
      <h3>{{ editingId ? 'Editar câmera' : 'Nova câmera' }}</h3>
      <form class="camera-form" @submit.prevent="saveCamera">
        <label>
          Nome
          <input v-model="form.name" type="text" required />
        </label>

        <label>
          Descrição
          <input v-model="form.description" type="text" />
        </label>

        <label>
          IP
          <input v-model="form.ip" type="text" required />
        </label>

        <label>
          Porta HTTP
          <input v-model.number="form.httpPort" type="number" required />
        </label>

        <label>
          Porta RTSP
          <input v-model.number="form.rtspPort" type="number" required />
        </label>

        <label>
          Usuário
          <input v-model="form.username" type="text" />
        </label>

        <label>
          Senha
          <input v-model="form.password" type="password" :placeholder="editingId ? 'Deixe vazio para manter' : ''" />
        </label>

        <label class="checkbox-row">
          <input v-model="form.active" type="checkbox" />
          Camera ativa
        </label>

        <div class="actions">
          <button type="submit" class="primary" aria-label="Adicionar câmera" title="Adicionar câmera">
            <i class="pi pi-camera" aria-hidden="true"></i>
          </button>
          <button type="button" class="secondary" @click="resetForm">Cancelar</button>
        </div>
      </form>
    </section>

    <section class="panel">
      <h3>Lista de câmeras</h3>
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>IP</th>
            <th>HTTP</th>
            <th>RTSP</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="camera in cameras" :key="camera.id">
            <td>{{ camera.name }}</td>
            <td>{{ camera.ip }}</td>
            <td>{{ camera.httpPort }}</td>
            <td>{{ camera.rtspPort }}</td>
            <td>{{ camera.active ? 'Ativa' : 'Inativa' }}</td>
            <td>
              <button class="small" @click="editCamera(camera)">Editar</button>
              <button class="small danger" @click="deleteCamera(camera.id)">Excluir</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</template>

<style scoped>
.camera-page {
  padding: 2rem;
  color: #1f2937;
  background: #e5e7eb;
  min-height: 100vh;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
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
  margin-bottom: 1.5rem;
}

.camera-form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
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

.checkbox-row {
  justify-content: center;
  flex-direction: row !important;
  align-items: center;
  margin-top: 1rem;
}

.actions {
  display: flex;
  gap: 0.75rem;
  align-items: end;
}

button {
  border: none;
  border-radius: 10px;
  padding: 0.8rem 1rem;
  min-height: 44px;
  cursor: pointer;
  font-weight: 600;
}

.primary {
  background: #22c55e;
  color: #062b12;
}

.secondary {
  background: #475569;
  color: white;
}

.small {
  padding: 0.45rem 0.7rem;
  margin-right: 0.4rem;
  background: #38bdf8;
  color: #082f49;
}

.danger {
  background: #ef4444;
  color: white;
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
