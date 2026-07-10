<template>
  <div class="p-5 sm:p-8 lg:p-14 flex flex-col gap-6 animate-fade-in">
    <div>
      <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white">TV ao Vivo</h2>
      <p class="text-xs text-text-muted mt-1">Canais carregados das suas playlists M3U</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <!-- Channel List -->
      <div class="lg:col-span-1 flex flex-col gap-2 max-h-[65vh] overflow-y-auto no-scrollbar glass-card p-4 rounded-2xl">
        <span class="text-[10px] font-bold text-text-muted uppercase tracking-wide px-2 mb-1">Canais</span>
        <button 
          v-for="(channel, index) in channels" 
          :key="index"
          :id="`iptv-${index}`"
          class="focusable p-3 rounded-xl text-left flex items-center gap-3 transition-smooth border"
          :class="activeChannel?.name === channel.name 
            ? 'bg-accent/10 border-accent/30 text-white' 
            : 'bg-transparent border-transparent text-text-secondary hover:text-white hover:bg-glass-hover'"
          @click="selectChannel(channel)"
        >
          <div class="w-9 h-9 rounded-lg bg-cinema-elevated overflow-hidden flex-shrink-0 border border-glass-border">
            <img :src="channel.logo" :alt="channel.name" class="w-full h-full object-cover" loading="lazy" />
          </div>
          <div class="truncate">
            <div class="text-[11px] font-semibold text-white truncate">{{ channel.name }}</div>
            <div class="text-[9px] text-text-muted font-medium uppercase mt-0.5">{{ channel.category }}</div>
          </div>
        </button>
        <div v-if="channels.length === 0" class="text-center py-8 text-xs text-text-muted">
          Nenhum canal. Adicione playlists M3U em Configurações.
        </div>
      </div>

      <!-- Player -->
      <div class="lg:col-span-3 flex flex-col gap-4">
        <div class="aspect-video w-full bg-black rounded-2xl overflow-hidden border border-glass-border relative shadow-hero">
          <video 
            v-if="activeChannel"
            :src="activeChannel.url" 
            autoplay 
            controls 
            muted
            class="w-full h-full object-cover"
          ></video>
          <div v-else class="w-full h-full flex items-center justify-center">
            <p class="text-sm text-text-muted">Selecione um canal</p>
          </div>
          <div v-if="activeChannel" class="absolute top-3 left-3 badge badge-live">
            AO VIVO
          </div>
        </div>
        <div v-if="activeChannel" class="glass-card p-4 rounded-2xl flex justify-between items-center">
          <div>
            <h3 class="text-base font-bold text-white">{{ activeChannel.name }}</h3>
            <p class="text-[10px] text-text-muted mt-0.5">{{ activeChannel.category }} · HD</p>
          </div>
          <button 
            @click="$emit('fullscreen', activeChannel)"
            class="focusable px-4 py-2 bg-accent hover:bg-accent-hover text-white text-xs font-bold rounded-xl transition-smooth flex items-center gap-1.5"
          >
            <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4"/></svg>
            Tela Cheia
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  channels: { type: Array, default: () => [] },
});

defineEmits(['fullscreen']);

const activeChannel = ref(props.channels[0] || null);

const selectChannel = (channel) => {
  activeChannel.value = channel;
};
</script>
