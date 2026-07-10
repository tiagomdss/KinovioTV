<template>
  <div class="fixed inset-0 z-[150] bg-gradient-to-b from-[#06060f] via-[#020205] to-[#0a0a14] flex flex-col items-center justify-center p-6 select-none animate-ios-fade-in overflow-hidden">
    <!-- Background blobs -->
    <div class="absolute w-[280px] h-[280px] sm:w-[500px] sm:h-[500px] rounded-full bg-accent/15 blur-[70px] sm:blur-[110px] -top-10 -left-10 animate-pulse"></div>
    <div class="absolute w-[350px] h-[350px] sm:w-[600px] sm:h-[600px] rounded-full bg-brand/15 blur-[90px] sm:blur-[130px] -bottom-10 -right-10 animate-pulse" style="animation-delay: 2s"></div>
    <div class="absolute w-[220px] h-[220px] rounded-full bg-rose-500/10 blur-[80px] top-[45%] left-[20%] animate-pulse" style="animation-delay: 4s"></div>
    <div class="absolute w-[280px] h-[280px] rounded-full bg-brand/10 blur-[85px] bottom-[-50px] left-[50%] -translate-x-1/2 pointer-events-none"></div>

    <!-- Content Card -->
    <div class="z-10 flex flex-col items-center gap-10 max-w-2xl w-full">
      <div class="flex flex-col items-center gap-2">
        <img src="~/assets/img/logo.png" alt="KinovioTV" class="w-16 h-16 object-contain animate-ios-bounce-in" />
        <h1 class="text-xl sm:text-2xl font-black text-white tracking-tight mt-4 text-center">Quem está assistindo?</h1>
        <p class="text-xs text-text-muted text-center">Selecione seu perfil ou crie um novo para recomendações personalizadas.</p>
      </div>

      <!-- Profiles Grid -->
      <div class="flex flex-wrap items-center justify-center gap-6 sm:gap-8">
        <button 
          v-for="profile in profiles" 
          :key="profile.name"
          class="focusable group flex flex-col items-center gap-3 bg-transparent border-0 outline-none cursor-pointer relative"
          @click="handleProfileClick($event, profile)"
          @mousedown="startPress(profile)"
          @mouseup="endPress"
          @mouseleave="endPress"
          @touchstart="startPress(profile)"
          @touchend="endPress"
          @touchmove="endPress"
        >
          <!-- Avatar Container -->
          <div class="relative w-20 h-20 sm:w-24 sm:h-24 rounded-2xl p-[1px] bg-gradient-to-tr from-glass-border to-glass-border group-hover:from-accent group-hover:to-brand shadow-lg transition-smooth duration-500 transform group-hover:scale-105 group-hover:rotate-1">
            
            <!-- Desktop Edit Pencil Overlay -->
            <button 
              type="button"
              class="hidden group-hover:flex absolute top-1.5 right-1.5 z-20 w-6 h-6 rounded-lg bg-black/75 hover:bg-accent border border-glass-border items-center justify-center text-white transition-smooth transform hover:scale-110"
              @click.stop="openEditModal(profile)"
              title="Editar perfil"
            >
              <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10"/></svg>
            </button>

            <div class="w-full h-full rounded-2xl bg-cinema-elevated flex items-center justify-center shadow-inner relative overflow-hidden">
              <!-- Glass sheen -->
              <span class="absolute inset-0 bg-gradient-to-br from-white/10 to-transparent pointer-events-none z-10"></span>
              <img 
                v-if="profile.avatar && (profile.avatar.startsWith('http') || profile.avatar.startsWith('/')) && !failedAvatars.includes(profile.name)" 
                :src="profile.avatar" 
                alt="Avatar" 
                class="w-full h-full object-cover"
                @error="handleImageError(profile)"
              />
              <span v-else class="text-4xl">{{ profile.avatar || '🍿' }}</span>
            </div>
            <!-- Profile active marker glow -->
            <div class="absolute inset-0 bg-accent/20 blur-xl opacity-0 group-hover:opacity-100 rounded-2xl transition-smooth duration-500 -z-10"></div>
          </div>
          <span class="text-xs sm:text-sm font-semibold text-text-secondary group-hover:text-white transition-smooth">{{ profile.name }}</span>
        </button>

        <!-- Add Profile Button -->
        <button 
          v-if="profiles.length < 5"
          @click="showCreateModal = true"
          class="focusable group flex flex-col items-center gap-3 bg-transparent border-0 outline-none cursor-pointer"
        >
          <div class="w-20 h-20 sm:w-24 sm:h-24 rounded-2xl border border-dashed border-glass-border group-hover:border-accent flex items-center justify-center transition-smooth hover:bg-glass transform group-hover:scale-105">
            <svg class="w-7 h-7 text-text-muted group-hover:text-accent transition-smooth" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4v16m8-8H4"/></svg>
          </div>
          <span class="text-xs sm:text-sm font-semibold text-text-muted group-hover:text-white transition-smooth">Adicionar</span>
        </button>
      </div>
    </div>

    <!-- Create Profile Dialog -->
    <div 
      v-if="showCreateModal" 
      class="fixed inset-0 z-[200] bg-black/60 backdrop-blur-md flex items-center justify-center p-4 animate-ios-fade-in"
      @click.self="showCreateModal = false"
    >
      <div class="glass-heavy border border-glass-border p-6 rounded-3xl max-w-sm w-full flex flex-col gap-5 animate-ios-scale-up">
        <div>
          <h3 class="text-sm sm:text-base font-bold text-white">Criar Novo Perfil</h3>
          <p class="text-[11px] text-text-muted mt-1">Insira o nome e escolha um ícone para seu novo perfil do KinovioTV.</p>
        </div>

        <div class="flex flex-col gap-4">
          <!-- Name Input -->
          <input 
            type="text" 
            v-model="newProfileName"
            placeholder="Nome do perfil"
            class="glass-card px-4 py-2.5 rounded-xl text-xs text-white focus:outline-none focus:border-accent/40 transition-smooth"
            @keyup.enter="createProfile"
          />

          <!-- Avatar Emojis Selector -->
          <div class="flex flex-col gap-2">
            <span class="text-[10px] font-bold text-text-muted uppercase tracking-wide">Emojis de Cinema</span>
            <div class="flex flex-wrap gap-2 justify-between">
              <button 
                v-for="emoji in ['🍿', '🎬', '🚀', '🎭', '👾', '🎧']" 
                :key="emoji"
                type="button"
                class="w-8 h-8 rounded-lg flex items-center justify-center text-lg transition-smooth border border-glass-border"
                :class="newProfileAvatar === emoji ? 'bg-accent text-white border-accent' : 'glass hover:bg-glass-hover text-text-secondary'"
                @click="newProfileAvatar = emoji"
              >
                {{ emoji }}
              </button>
            </div>
          </div>

          <!-- Avatar Famous Characters Selector -->
          <div class="flex flex-col gap-2">
            <span class="text-[10px] font-bold text-text-muted uppercase tracking-wide">Escolher Imagem de Avatar</span>
            <div class="flex gap-2 overflow-x-auto no-scrollbar py-2 w-full max-w-full">
              <button 
                v-for="avatarPath in availableAvatars" 
                :key="avatarPath"
                type="button"
                class="w-10 h-10 rounded-xl overflow-hidden border shrink-0 transition-smooth relative"
                :class="newProfileAvatar === avatarPath ? 'border-accent scale-105 shadow-md shadow-accent/25' : 'border-glass-border hover:border-glass-hover opacity-70 hover:opacity-100'"
                @click="newProfileAvatar = avatarPath"
              >
                <img :src="avatarPath" alt="Avatar Option" class="w-full h-full object-cover" />
              </button>
            </div>
          </div>
        </div>

        <div class="flex gap-2.5 mt-2">
          <button 
            @click="showCreateModal = false"
            class="px-4 py-2.5 bg-glass border border-glass-border hover:bg-glass-hover text-text-secondary hover:text-white text-xs font-bold rounded-xl transition-smooth flex-1"
          >
            Cancelar
          </button>
          <button 
            @click="createProfile"
            class="focusable px-4 py-2.5 bg-accent hover:bg-accent-hover text-white text-xs font-bold rounded-xl transition-smooth flex-1"
            :disabled="!newProfileName.trim()"
            :class="!newProfileName.trim() ? 'opacity-50 cursor-not-allowed' : ''"
          >
            Criar Perfil
          </button>
        </div>
      </div>
    </div>

    <!-- Edit Profile Dialog -->
    <div 
      v-if="showEditModal" 
      class="fixed inset-0 z-[200] bg-black/60 backdrop-blur-md flex items-center justify-center p-4 animate-ios-fade-in"
      @click.self="closeEditModal"
    >
      <div class="glass-heavy border border-glass-border p-6 rounded-3xl max-w-sm w-full flex flex-col gap-5 animate-ios-scale-up">
        <div>
          <h3 class="text-sm sm:text-base font-bold text-white">Editar Perfil</h3>
          <p class="text-[11px] text-text-muted mt-1">Altere o nome e o avatar do seu perfil do KinovioTV.</p>
        </div>

        <div class="flex flex-col gap-4">
          <!-- Name Input -->
          <input 
            type="text" 
            v-model="editName"
            placeholder="Nome do perfil"
            class="glass-card px-4 py-2.5 rounded-xl text-xs text-white focus:outline-none focus:border-accent/40 transition-smooth"
            @keyup.enter="saveEditProfile"
          />

          <!-- Avatar Emojis Selector -->
          <div class="flex flex-col gap-2">
            <span class="text-[10px] font-bold text-text-muted uppercase tracking-wide">Emojis de Cinema</span>
            <div class="flex flex-wrap gap-2 justify-between">
              <button 
                v-for="emoji in ['🍿', '🎬', '🚀', '🎭', '👾', '🎧']" 
                :key="emoji"
                type="button"
                class="w-8 h-8 rounded-lg flex items-center justify-center text-lg transition-smooth border border-glass-border"
                :class="editAvatar === emoji ? 'bg-accent text-white border-accent' : 'glass hover:bg-glass-hover text-text-secondary'"
                @click="editAvatar = emoji"
              >
                {{ emoji }}
              </button>
            </div>
          </div>

          <!-- Avatar Famous Characters Selector -->
          <div class="flex flex-col gap-2">
            <span class="text-[10px] font-bold text-text-muted uppercase tracking-wide">Escolher Imagem de Avatar</span>
            <div class="flex gap-2 overflow-x-auto no-scrollbar py-2 w-full max-w-full">
              <button 
                v-for="avatarPath in availableAvatars" 
                :key="avatarPath"
                type="button"
                class="w-10 h-10 rounded-xl overflow-hidden border shrink-0 transition-smooth relative"
                :class="editAvatar === avatarPath ? 'border-accent scale-105 shadow-md shadow-accent/25' : 'border-glass-border hover:border-glass-hover opacity-70 hover:opacity-100'"
                @click="editAvatar = avatarPath"
              >
                <img :src="avatarPath" alt="Avatar Option" class="w-full h-full object-cover" />
              </button>
            </div>
          </div>
        </div>

        <div class="flex flex-col gap-2 mt-2">
          <div class="flex gap-2.5">
            <button 
              @click="closeEditModal"
              class="px-4 py-2.5 bg-glass border border-glass-border hover:bg-glass-hover text-text-secondary hover:text-white text-xs font-bold rounded-xl transition-smooth flex-1"
            >
              Cancelar
            </button>
            <button 
              @click="saveEditProfile"
              class="focusable px-4 py-2.5 bg-accent hover:bg-accent-hover text-white text-xs font-bold rounded-xl transition-smooth flex-1"
              :disabled="!editName.trim()"
              :class="!editName.trim() ? 'opacity-50 cursor-not-allowed' : ''"
            >
              Salvar
            </button>
          </div>
          <button 
            v-if="profiles.length > 1"
            @click="deleteProfile"
            class="w-full py-2 bg-rose-500/10 border border-rose-500/20 hover:bg-rose-500/20 text-rose-400 hover:text-rose-300 text-[10px] font-bold rounded-xl transition-smooth"
          >
            🗑️ Excluir Perfil
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

defineProps({
  profiles: { type: Array, required: true }
});

const emit = defineEmits(['select', 'create', 'edit', 'delete']);

const availableAvatars = Array.from({ length: 30 }, (_, i) => `/avatars/avatar_${i + 1}.png`);

const showCreateModal = ref(false);
const newProfileName = ref('');
const newProfileAvatar = ref('/avatars/avatar_1.png');

const showEditModal = ref(false);
const editingProfile = ref(null);
const editName = ref('');
const editAvatar = ref('');

// Long-press Touch Hold variables
let pressTimer = null;
let isLongPress = false;

const startPress = (profile) => {
  isLongPress = false;
  endPress();
  pressTimer = setTimeout(() => {
    isLongPress = true;
    openEditModal(profile);
  }, 750); // Segurar por 750ms no celular abre o painel de edição!
};

const endPress = () => {
  if (pressTimer) {
    clearTimeout(pressTimer);
    pressTimer = null;
  }
};

const handleProfileClick = (event, profile) => {
  // Se o temporizador de Hold disparou a edição, cancelamos o clique de login padrão!
  if (isLongPress) {
    isLongPress = false;
    return;
  }
  endPress();
  emit('select', profile);
};

const createProfile = () => {
  if (!newProfileName.value.trim()) return;
  emit('create', {
    name: newProfileName.value.trim(),
    avatar: newProfileAvatar.value
  });
  newProfileName.value = '';
  newProfileAvatar.value = '🍿';
  showCreateModal.value = false;
};

const openEditModal = (profile) => {
  endPress();
  editingProfile.value = profile;
  editName.value = profile.name;
  editAvatar.value = profile.avatar;
  showEditModal.value = true;
};

const closeEditModal = () => {
  showEditModal.value = false;
  editingProfile.value = null;
};

const saveEditProfile = () => {
  if (!editName.value.trim() || !editingProfile.value) return;
  emit('edit', {
    oldName: editingProfile.value.name,
    name: editName.value.trim(),
    avatar: editAvatar.value
  });
  closeEditModal();
};

const deleteProfile = () => {
  if (!editingProfile.value) return;
  emit('delete', editingProfile.value.name);
  closeEditModal();
};

const failedAvatars = ref([]);

const handleImageError = (profile) => {
  failedAvatars.value.push(profile.name);
};

const handleNewAvatarError = (e) => {
  e.target.style.display = 'none';
};
</script>

<style scoped>
.animate-ios-fade-in {
  animation: fadeIn 0.4s cubic-bezier(0.25, 1, 0.2, 1) forwards;
}
.animate-ios-scale-up {
  animation: scaleUp 0.45s cubic-bezier(0.25, 1, 0.2, 1.1) forwards;
}
.animate-ios-bounce-in {
  animation: bounceIn 0.6s cubic-bezier(0.25, 1, 0.2, 1.1) forwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes scaleUp {
  from { transform: scale(0.92); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

@keyframes bounceIn {
  from { transform: scale(0.8) translateY(10px); opacity: 0; }
  to { transform: scale(1) translateY(0); opacity: 1; }
}
</style>
