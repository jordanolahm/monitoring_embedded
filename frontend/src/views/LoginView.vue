<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const username = ref('admin')
const password = ref('admin123')
const loading = ref(false)
const error = ref('')
const router = useRouter()

async function login() {
  loading.value = true
  error.value = ''

  try {
    const response = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })

    if (response.data.success) {
      const payload = response.data.data ?? {}
      localStorage.setItem('monitoramento-auth', 'true')
      localStorage.setItem('monitoramento-username', username.value)
      localStorage.setItem('monitoramento-token', payload.token ?? '')
      router.push('/dashboard')
    } else {
      error.value = response.data.message || 'Falha ao autenticar.'
    }
  } catch (err: unknown) {
    error.value = 'Credenciais inválidas ou backend indisponível.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-shell">
    <div class="login-card">
      <h1>Monitoramento Embarcado</h1>
      <p>Entre com suas credenciais</p>

      <form @submit.prevent="login">
        <label>
          Usuário
          <input v-model="username" type="text" />
        </label>

        <label>
          Senha
          <input v-model="password" type="password" />
        </label>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Entrando...' : 'Entrar' }}
        </button>
      </form>

      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #f3f4f6, #d1d5db);
  color: #1f2937;
}

.login-card {
  width: min(100%, 420px);
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(107, 114, 128, 0.2);
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.4);
}

h1 {
  margin-bottom: 0.5rem;
}

p {
  margin-bottom: 1.5rem;
}

form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  font-size: 0.95rem;
}

input {
  border-radius: 10px;
  border: 1px solid #9ca3af;
  background: white;
  color: #1f2937;
  padding: 0.75rem 0.9rem;
}

button {
  margin-top: 0.5rem;
  padding: 0.9rem 1rem;
  border: none;
  border-radius: 10px;
  background: #22c55e;
  color: #062b12;
  font-weight: 700;
  cursor: pointer;
}

.error {
  margin-top: 1rem;
  color: #b91c1c;
}
</style>
