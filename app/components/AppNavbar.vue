<template>
  <!-- Desktop Sidebar -->
  <nav class="hidden md:flex fixed left-0 top-0 h-screen w-[var(--nav-width-desktop)] flex-col items-center pt-6 pb-6 z-50 glass-heavy border-r border-glass-border">
    <!-- Logo -->
    <div class="relative mb-10 group cursor-pointer" @click="$emit('navigate', 'home')">
      <div class="w-11 h-11 rounded-xl overflow-hidden flex items-center justify-center transition-smooth group-hover:scale-110">
        <img src="~/assets/img/logo.png" alt="KinovioTV Logo" class="w-full h-full object-cover" />
      </div>
      <div class="absolute inset-0 bg-accent blur-xl opacity-0 group-hover:opacity-20 rounded-xl transition-smooth"></div>
    </div>

    <!-- Nav Items -->
    <div class="flex flex-col items-center gap-1.5 flex-1">
      <button 
        v-for="item in navItems" 
        :key="item.id"
        :id="item.id"
        class="focusable relative w-11 h-11 rounded-xl flex items-center justify-center transition-smooth group/nav"
        :class="currentTab === item.tab 
          ? 'text-white bg-accent/15' 
          : 'text-text-muted hover:text-white hover:bg-glass-hover'"
        @click="$emit('navigate', item.tab)"
        :title="item.label"
      >
        <component :is="item.icon" :size="20" :stroke-width="currentTab === item.tab ? 2.5 : 1.5" />
        
        <!-- Active indicator -->
        <span 
          v-if="currentTab === item.tab" 
          class="absolute -right-[1px] top-1/2 -translate-y-1/2 w-[3px] h-5 rounded-l-full bg-accent"
        ></span>

        <!-- Tooltip -->
        <span class="absolute left-full ml-3 px-2.5 py-1 rounded-lg text-[10px] font-semibold text-white bg-cinema-elevated border border-glass-border whitespace-nowrap opacity-0 group-hover/nav:opacity-100 pointer-events-none transition-fast z-50">
          {{ item.label }}
        </span>
      </button>
    </div>

    <!-- Bottom: Settings + Profile -->
    <div class="flex flex-col items-center gap-3">
      <!-- Settings Button -->
      <button 
        id="nav-settings"
        class="focusable w-11 h-11 rounded-xl flex items-center justify-center transition-smooth text-text-muted hover:text-white hover:bg-glass-hover"
        :class="currentTab === 'settings' ? 'text-white bg-accent/15' : ''"
        @click="$emit('navigate', 'settings')"
        title="Configurações"
      >
        <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" :stroke-width="currentTab === 'settings' ? 2.5 : 1.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9.594 3.94c.09-.542.56-.94 1.11-.94h2.593c.55 0 1.02.398 1.11.94l.213 1.281c.063.374.313.686.645.87.074.04.147.083.22.127.324.196.72.257 1.075.124l1.217-.456a1.125 1.125 0 011.37.49l1.296 2.247a1.125 1.125 0 01-.26 1.431l-1.003.827c-.293.24-.438.613-.431.992a6.759 6.759 0 010 .255c-.007.378.138.75.43.99l1.005.828c.424.35.534.954.26 1.43l-1.298 2.247a1.125 1.125 0 01-1.369.491l-1.217-.456c-.355-.133-.75-.072-1.076.124a6.57 6.57 0 01-.22.128c-.331.183-.581.495-.644.869l-.213 1.28c-.09.543-.56.941-1.11.941h-2.594c-.55 0-1.02-.398-1.11-.94l-.213-1.281c-.062-.374-.312-.686-.644-.87a6.52 6.52 0 01-.22-.127c-.325-.196-.72-.257-1.076-.124l-1.217.456a1.125 1.125 0 01-1.369-.49l-1.297-2.247a1.125 1.125 0 01.26-1.431l1.004-.827c.292-.24.437-.613.43-.992a6.932 6.932 0 010-.255c.007-.378-.138-.75-.43-.99l-1.004-.828a1.125 1.125 0 01-.26-1.43l1.297-2.247a1.125 1.125 0 011.37-.491l1.216.456c.356.133.751.072 1.076-.124.072-.044.146-.087.22-.128.332-.183.582-.495.644-.869l.214-1.281z"/><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/></svg>
      </button>

      <!-- Profile Avatar Button -->
      <button 
        id="nav-profile-select"
        class="focusable w-9 h-9 rounded-xl flex items-center justify-center bg-gradient-to-tr from-glass-border to-glass-border hover:from-accent hover:to-brand shadow-md transition-smooth transform hover:scale-105 active:scale-95 overflow-hidden"
        @click="$emit('change-profile')"
        :title="`Perfil: ${activeProfile?.name || 'Selecionar'}`"
      >
        <img 
          v-if="activeProfile?.avatar && (activeProfile.avatar.startsWith('http') || activeProfile.avatar.startsWith('/')) && !failedActiveAvatar" 
          :src="activeProfile.avatar" 
          alt="Profile Pic" 
          class="w-full h-full object-cover"
          @error="handleAvatarError"
        />
        <span v-else class="text-base">{{ activeProfile?.avatar || '🍿' }}</span>
      </button>
    </div>
  </nav>

  <!-- Mobile Bottom Bar -->
  <nav class="md:hidden fixed bottom-0 left-0 right-0 z-50 px-3 pb-2 safe-bottom pointer-events-none" aria-label="Navegação principal">
    <div class="pointer-events-auto flex items-center justify-around h-[68px] px-1 rounded-2xl glass-heavy border border-white/10 shadow-2xl shadow-black/50">
      <button 
        v-for="item in mobileNavItems" 
        :key="item.id"
        :id="`m-${item.id}`"
        class="focusable relative flex flex-col items-center justify-center gap-1 py-2 px-1 rounded-xl transition-fast min-w-[58px] min-h-[52px]"
        :class="currentTab === item.tab 
          ? 'text-accent' 
          : 'text-text-muted'"
        @click="$emit('navigate', item.tab)"
      >
        <component :is="item.icon" :size="20" :stroke-width="currentTab === item.tab ? 2.5 : 1.5" />
        <span v-if="currentTab === item.tab" class="absolute top-0 w-5 h-0.5 rounded-full bg-accent"></span>
        <span class="text-[10px] font-semibold">{{ item.label }}</span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { ref, watch } from 'vue';
import { Home, Search, Heart, Radio, Compass, CalendarDays, Bookmark, UserRound } from 'lucide-vue-next';

const props = defineProps({
  currentTab: { type: String, default: 'home' },
  activeProfile: { type: Object, default: () => ({ name: 'Tiago', avatar: '🍿' }) }
});

defineEmits(['navigate', 'change-profile']);

const failedActiveAvatar = ref(false);
const handleAvatarError = () => {
  failedActiveAvatar.value = true;
};

// Reset image error status when profile switches
watch(() => props.activeProfile, () => {
  failedActiveAvatar.value = false;
}, { deep: true });

// Desktop sidebar: 4 items + settings/profile at bottom
const navItems = [
  { id: 'nav-home', tab: 'home', label: 'Início', icon: Home },
  { id: 'nav-search', tab: 'search', label: 'Buscar', icon: Search },
  { id: 'nav-calendar', tab: 'calendar', label: 'Calendário', icon: CalendarDays },
  { id: 'nav-favorites', tab: 'favorites', label: 'Favoritos', icon: Heart },
  { id: 'nav-livetv', tab: 'livetv', label: 'TV ao Vivo', icon: Radio },
];

// Mobile bottom bar: 4 items + profile
const mobileNavItems = [
  { id: 'nav-home', tab: 'home', label: 'Início', icon: Home },
  { id: 'nav-search', tab: 'search', label: 'Explorar', icon: Compass },
  { id: 'nav-calendar', tab: 'calendar', label: 'Calendário', icon: CalendarDays },
  { id: 'nav-favorites', tab: 'favorites', label: 'Minha lista', icon: Bookmark },
  { id: 'nav-profile', tab: 'profile', label: 'Perfil', icon: UserRound },
];
</script>
