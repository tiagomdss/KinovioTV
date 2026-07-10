<template>
  <section class="min-h-screen px-5 pt-8 pb-32 sm:px-8 lg:px-14 lg:pt-12">
    <div class="mx-auto max-w-4xl">
      <header class="flex items-center justify-between gap-4">
        <div>
          <p class="text-[11px] font-bold uppercase tracking-[0.18em] text-accent">Seu espaço</p>
          <h1 class="mt-2 text-3xl font-black tracking-tight text-white sm:text-5xl">Perfil</h1>
        </div>
        <button class="focusable flex min-h-11 items-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-4 text-sm font-semibold text-white hover:bg-white/[0.08]" @click="$emit('settings')">
          <Settings :size="17" />
          <span class="hidden sm:inline">Configurações</span>
        </button>
      </header>

      <div class="mt-8 overflow-hidden rounded-2xl border border-white/10 bg-white/[0.035]">
        <div class="relative flex flex-col items-center gap-5 p-7 text-center sm:flex-row sm:text-left">
          <div class="absolute inset-x-0 top-0 h-24 bg-gradient-to-r from-accent/20 via-brand/10 to-transparent blur-2xl"></div>
          <div class="relative h-24 w-24 shrink-0 overflow-hidden rounded-2xl border border-white/15 bg-cinema-surface shadow-xl shadow-black/30">
            <img v-if="isImageAvatar" :src="profile.avatar" :alt="`Avatar de ${profile.name}`" class="h-full w-full object-cover" />
            <div v-else class="flex h-full w-full items-center justify-center bg-gradient-to-br from-accent to-brand text-3xl font-black text-white">{{ initial }}</div>
          </div>
          <div class="relative min-w-0 flex-1">
            <h2 class="truncate text-2xl font-black text-white">{{ profile.name || 'Perfil Kinovio' }}</h2>
            <p class="mt-1 text-sm text-text-secondary">Preferências, histórico e lista independentes.</p>
            <div class="mt-4 flex flex-wrap justify-center gap-2 sm:justify-start">
              <span class="inline-flex items-center gap-1.5 rounded-lg border border-white/8 bg-black/20 px-3 py-1.5 text-xs text-text-secondary"><ShieldCheck :size="14" /> Perfil protegido</span>
              <span class="inline-flex items-center gap-1.5 rounded-lg border border-white/8 bg-black/20 px-3 py-1.5 text-xs text-text-secondary"><Languages :size="14" /> Português</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-2 border-t border-white/8 sm:grid-cols-4">
          <div v-for="stat in stats" :key="stat.label" class="border-r border-white/8 px-4 py-5 text-center last:border-r-0">
            <strong class="block text-xl font-black tabular-nums text-white">{{ stat.value }}</strong>
            <span class="mt-1 block text-[10px] font-semibold uppercase tracking-wide text-text-muted">{{ stat.label }}</span>
          </div>
        </div>
      </div>

      <div class="mt-6 grid gap-3 sm:grid-cols-2">
        <button class="focusable group flex min-h-[84px] items-center gap-4 rounded-2xl border border-white/8 bg-white/[0.035] p-4 text-left hover:border-accent/30 hover:bg-white/[0.06]" @click="$emit('list')">
          <span class="flex h-11 w-11 items-center justify-center rounded-xl bg-accent/15 text-accent"><Bookmark :size="21" /></span>
          <span class="min-w-0 flex-1"><strong class="block text-sm text-white">Minha lista</strong><small class="mt-1 block text-xs text-text-secondary">Títulos salvos neste perfil</small></span>
          <ChevronRight :size="18" class="text-text-muted transition-transform group-hover:translate-x-1" />
        </button>
        <button class="focusable group flex min-h-[84px] items-center gap-4 rounded-2xl border border-white/8 bg-white/[0.035] p-4 text-left hover:border-accent/30 hover:bg-white/[0.06]" @click="$emit('change-profile')">
          <span class="flex h-11 w-11 items-center justify-center rounded-xl bg-brand/15 text-brand"><Users :size="21" /></span>
          <span class="min-w-0 flex-1"><strong class="block text-sm text-white">Trocar perfil</strong><small class="mt-1 block text-xs text-text-secondary">Mudar sem sair do aplicativo</small></span>
          <ChevronRight :size="18" class="text-text-muted transition-transform group-hover:translate-x-1" />
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue';
import { Bookmark, ChevronRight, Languages, Settings, ShieldCheck, Users } from 'lucide-vue-next';

const props = defineProps({
  profile: { type: Object, default: () => ({}) },
  favoritesCount: { type: Number, default: 0 },
  watchingCount: { type: Number, default: 0 },
});
defineEmits(['settings', 'list', 'change-profile']);

const initial = computed(() => (props.profile?.name || 'K').trim().charAt(0).toUpperCase());
const isImageAvatar = computed(() => typeof props.profile?.avatar === 'string' && (props.profile.avatar.startsWith('/') || props.profile.avatar.startsWith('http')));
const stats = computed(() => [
  { label: 'Assistindo', value: props.watchingCount },
  { label: 'Minha lista', value: props.favoritesCount },
  { label: 'Concluídos', value: props.profile?.watched?.length || 0 },
  { label: 'Lembretes', value: props.profile?.reminders?.length || 0 },
]);
</script>
