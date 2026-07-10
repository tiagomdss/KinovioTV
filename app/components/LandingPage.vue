<script setup>
import { ref } from 'vue';

const emit = defineEmits(['auth-success']);

const isLogin = ref(true);
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const errorMsg = ref('');
const loading = ref(false);

const toggleMode = () => {
  isLogin.value = !isLogin.value;
  errorMsg.value = '';
  password.value = '';
  confirmPassword.value = '';
};

const validateForm = () => {
  if (!email.value || !password.value) {
    errorMsg.value = 'Preencha todos os campos.';
    return false;
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email.value)) {
    errorMsg.value = 'Por favor, insira um e-mail válido.';
    return false;
  }
  if (password.value.length < 6) {
    errorMsg.value = 'A senha deve conter no mínimo 6 caracteres.';
    return false;
  }
  if (!isLogin.value && password.value !== confirmPassword.value) {
    errorMsg.value = 'As senhas não coincidem.';
    return false;
  }
  return true;
};

const handleSubmit = async () => {
  if (!validateForm()) return;

  loading.value = true;
  errorMsg.value = '';

  const endpoint = isLogin.value ? '/api/auth/login' : '/api/auth/signup';
  try {
    const response = await $fetch(endpoint, {
      method: 'POST',
      body: {
        email: email.value,
        password: password.value
      }
    });

    if (response.success) {
      emit('auth-success', response.user);
    }
  } catch (err) {
    errorMsg.value = err.data?.statusMessage || 'Ocorreu um erro no servidor.';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="relative w-screen h-screen flex items-center justify-center bg-cinema-deep overflow-hidden font-sans select-none">
    
    <!-- Background mesh & orbs -->
    <div class="absolute inset-0 z-0">
      <div class="absolute inset-0 bg-[radial-gradient(ellipse_at_center,rgba(10,10,15,0.2),rgba(2,2,3,0.95))] z-10"></div>
      <div class="absolute top-[-10%] left-[-10%] w-[600px] h-[600px] bg-accent/20 rounded-full blur-[140px] animate-pulse pointer-events-none"></div>
      <div class="absolute bottom-[-10%] right-[-10%] w-[600px] h-[600px] bg-brand/20 rounded-full blur-[140px] animate-pulse pointer-events-none" style="animation-delay: 3s;"></div>
      
      <!-- Premium pattern background -->
      <div class="absolute inset-0 opacity-10 bg-[linear-gradient(to_right,#80808012_1px,transparent_1px),linear-gradient(to_bottom,#80808012_1px,transparent_1px)] bg-[size:24px_24px]"></div>
    </div>

    <!-- Main Container -->
    <div class="relative z-10 w-full max-w-[420px] mx-4 animate-scale-in">
      
      <!-- Brand Header -->
      <div class="flex flex-col items-center mb-8">
        <div class="flex items-center gap-3">
          <img src="~/assets/img/logo.png" alt="KinovioTV Logo" class="w-12 h-12 object-contain" />
          <h1 class="text-3xl font-black tracking-tight text-white">Kinovio<span class="text-transparent bg-clip-text bg-gradient-to-r from-accent to-brand">TV</span></h1>
        </div>
        <p class="text-xs text-text-secondary mt-2.5 font-medium tracking-wide">Seu portal pessoal de cinema em nuvem</p>
      </div>

      <!-- Auth Form Card -->
      <div class="glass-heavy border border-glass-border rounded-3xl p-8 sm:p-10 shadow-premium flex flex-col gap-6 relative overflow-hidden">
        
        <!-- Ambient internal glow -->
        <div class="absolute -top-10 -right-10 w-32 h-32 bg-accent/10 rounded-full blur-2xl pointer-events-none"></div>

        <div>
          <h2 class="text-2xl font-black text-white mb-1.5">{{ isLogin ? 'Entrar' : 'Criar Conta' }}</h2>
          <p class="text-xs text-text-secondary font-medium">
            {{ isLogin ? 'Faça login para sincronizar seu cinema' : 'Cadastre-se para conectar seus streamings' }}
          </p>
        </div>

        <!-- Form -->
        <form @submit.prevent="handleSubmit" class="flex flex-col gap-4">
          
          <!-- Error feedback -->
          <div v-if="errorMsg" class="px-4 py-3 rounded-xl border border-red-500/20 bg-red-500/5 text-red-400 text-xs font-semibold animate-slide-down flex items-center gap-2">
            <svg class="w-4 h-4 shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>
            <span>{{ errorMsg }}</span>
          </div>

          <!-- Email Input -->
          <div class="flex flex-col gap-1.5">
            <label class="text-[10px] font-bold text-text-secondary tracking-wider uppercase">E-mail</label>
            <input 
              v-model="email"
              type="email"
              placeholder="seuemail@exemplo.com"
              class="w-full px-4 py-3 rounded-xl bg-glass border border-glass-border text-white text-xs font-semibold placeholder:text-text-muted focus:border-accent/40 focus:bg-glass-hover focus:shadow-glow-accent outline-none transition-smooth"
              required
            />
          </div>

          <!-- Password Input -->
          <div class="flex flex-col gap-1.5">
            <label class="text-[10px] font-bold text-text-secondary tracking-wider uppercase">Senha</label>
            <input 
              v-model="password"
              type="password"
              placeholder="Mínimo 6 caracteres"
              class="w-full px-4 py-3 rounded-xl bg-glass border border-glass-border text-white text-xs font-semibold placeholder:text-text-muted focus:border-accent/40 focus:bg-glass-hover focus:shadow-glow-accent outline-none transition-smooth"
              required
            />
          </div>

          <!-- Confirm Password Input (Signup only) -->
          <div v-if="!isLogin" class="flex flex-col gap-1.5 animate-slide-up">
            <label class="text-[10px] font-bold text-text-secondary tracking-wider uppercase">Confirmar Senha</label>
            <input 
              v-model="confirmPassword"
              type="password"
              placeholder="Digite a senha novamente"
              class="w-full px-4 py-3 rounded-xl bg-glass border border-glass-border text-white text-xs font-semibold placeholder:text-text-muted focus:border-accent/40 focus:bg-glass-hover focus:shadow-glow-accent outline-none transition-smooth"
              required
            />
          </div>

          <!-- Submit Button -->
          <button 
            type="submit"
            :disabled="loading"
            class="w-full py-3.5 mt-2 rounded-xl bg-accent hover:bg-accent-hover text-white text-xs font-bold transition-smooth flex items-center justify-center gap-2 shadow-lg shadow-accent/20 hover:shadow-accent/40 focus:scale-[0.98]"
          >
            <span v-if="loading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
            <span v-else>{{ isLogin ? 'Acessar Catálogo' : 'Finalizar Cadastro' }}</span>
          </button>
        </form>

        <!-- Toggle Auth Mode -->
        <div class="text-center pt-2 border-t border-glass-border/40">
          <button 
            @click="toggleMode"
            class="text-xs text-text-secondary hover:text-white font-medium transition-smooth"
          >
            {{ isLogin ? 'Novo no KinovioTV? ' : 'Já possui conta? ' }}
            <span class="text-accent hover:text-accent-hover font-bold">{{ isLogin ? 'Assine Já' : 'Entrar' }}</span>
          </button>
        </div>

      </div>

      <!-- Footer Info -->
      <p class="text-center text-[10px] text-text-muted mt-8 font-medium">
        KinovioTV utiliza Stremio, Jellyfin, IPTV e RealDebrid. <br/>
        Não hospedamos arquivos diretamente.
      </p>

    </div>
  </div>
</template>
