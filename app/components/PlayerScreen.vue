<template>
  <div class="fixed inset-0 z-[100] bg-black flex items-center justify-center overflow-hidden animate-ios-scale-up">
    <!-- Video element with subtitle tracks -->
    <video 
      v-if="activeStream"
      ref="videoEl"
      :src="activeStream.url" 
      autoplay
      class="w-full h-full object-contain"
      @timeupdate="onTimeUpdate"
      @click="toggleControls"
    >
      <track
        v-for="sub in subtitles"
        :key="sub.id"
        :src="sub.url"
        :srclang="sub.lang"
        :label="sub.label"
        kind="subtitles"
        :default="selectedSubtitleId === sub.id"
      />
    </video>

    <!-- Controls Overlay -->
    <div 
      class="absolute inset-0 bg-gradient-to-t from-black/90 via-transparent to-black/70 flex flex-col justify-between p-4 sm:p-6 transition-all duration-300 z-10"
      :class="showControls ? 'opacity-100' : 'opacity-0 pointer-events-none'"
      @click.self="toggleControls"
    >
      <!-- Top Bar -->
      <div class="flex justify-between items-start">
        <div class="flex items-center gap-3">
          <button 
            id="btn-player-back"
            @click="$emit('exit')"
            class="focusable p-2.5 rounded-xl glass-card text-white transition-smooth"
          >
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
          </button>
          <div>
            <h4 class="text-sm font-bold text-white">{{ title }}</h4>
            <p class="text-[10px] text-text-muted mt-0.5 truncate max-w-[200px] sm:max-w-lg">{{ activeStream?.name }}</p>
          </div>
        </div>

        <!-- Stream Info -->
        <div class="hidden sm:flex items-center gap-3 glass-card px-3 py-2 rounded-xl text-[10px] font-semibold text-text-secondary">
          <span v-if="activeStream?.quality" class="text-brand">{{ activeStream.quality }}</span>
          <span v-else class="text-accent">Auto</span>
          <span v-if="activeStream?.size">{{ activeStream.size }}</span>
        </div>
      </div>

      <!-- Center: Play/Pause -->
      <div class="flex items-center justify-center gap-6">
        <button class="focusable p-3 rounded-full text-white/60 hover:text-white transition-smooth" @click="seek(-10)">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12.066 11.2a1 1 0 000 1.6l5.334 4A1 1 0 0019 16V8a1 1 0 00-1.6-.8l-5.333 4zM4.066 11.2a1 1 0 000 1.6l5.334 4A1 1 0 0011 16V8a1 1 0 00-1.6-.8l-5.334 4z"/></svg>
        </button>
        <button 
          id="btn-player-toggle"
          @click="togglePlay"
          class="focusable p-5 rounded-full bg-white/10 border border-white/20 text-white hover:bg-white/20 transition-smooth hover:scale-110"
        >
          <svg v-if="isPlaying" class="w-8 h-8" fill="currentColor" viewBox="0 0 24 24"><path d="M6 4h4v16H6V4zm8 0h4v16h-4V4z"/></svg>
          <svg v-else class="w-8 h-8 ml-0.5" fill="currentColor" viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg>
        </button>
        <button class="focusable p-3 rounded-full text-white/60 hover:text-white transition-smooth" @click="seek(10)">
          <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M11.933 12.8a1 1 0 000-1.6L6.6 7.2A1 1 0 005 8v8a1 1 0 001.6.8l5.333-4zM19.933 12.8a1 1 0 000-1.6l-5.333-4A1 1 0 0013 8v8a1 1 0 001.6.8l5.333-4z"/></svg>
        </button>
      </div>

      <!-- Bottom Controls -->
      <div class="flex flex-col gap-3 max-w-5xl mx-auto w-full glass-card p-4 rounded-2xl relative">
        
        <!-- Source Selector Overlay Menu -->
        <div 
          v-if="showSourceMenu" 
          class="absolute bottom-full left-4 right-4 mb-3 p-4 rounded-2xl glass-heavy border border-glass-border max-h-[220px] overflow-y-auto no-scrollbar flex flex-col gap-1.5 z-20 animate-ios-slide-up"
        >
          <div class="flex justify-between items-center mb-1 pb-1 border-b border-glass-border">
            <span class="text-[10px] font-black text-white uppercase tracking-wider">Fontes Disponíveis</span>
            <button @click="showSourceMenu = false" class="text-text-muted hover:text-white text-[10px] font-bold">Fechar</button>
          </div>
          <button
            v-for="(src, idx) in streams"
            :key="idx"
            class="focusable w-full p-2.5 rounded-lg text-left text-[10px] flex items-center justify-between transition-smooth border border-glass-border/30 hover:border-accent/40 hover:bg-accent/5"
            :class="activeStream?.url === src.url ? 'border-accent bg-accent/5 font-bold text-accent' : 'text-text-secondary'"
            @click="selectStream(src)"
          >
            <span class="truncate max-w-[80%]">{{ src.name }}</span>
            <span class="text-[8px] bg-cinema-deep px-1.5 py-0.5 rounded border border-glass-border whitespace-nowrap">{{ src.size || 'Auto' }}</span>
          </button>
          <div v-if="streams.length === 0" class="text-center py-4 text-[10px] text-text-muted">
            Nenhuma fonte alternativa encontrada.
          </div>
        </div>

        <!-- Subtitles Selector Overlay Menu -->
        <div 
          v-if="showSubtitleMenu" 
          class="absolute bottom-full right-4 w-[180px] mb-3 p-4 rounded-2xl glass-heavy border border-glass-border max-h-[220px] overflow-y-auto no-scrollbar flex flex-col gap-1.5 z-20 animate-ios-slide-up"
        >
          <div class="flex justify-between items-center mb-1 pb-1 border-b border-glass-border">
            <span class="text-[10px] font-black text-white uppercase tracking-wider">Legendas</span>
            <button @click="showSubtitleMenu = false" class="text-text-muted hover:text-white text-[10px] font-bold">Fechar</button>
          </div>
          <button
            class="focusable w-full p-2 rounded-lg text-left text-[10px] transition-smooth border"
            :class="!selectedSubtitleId ? 'border-accent text-accent font-bold bg-accent/5' : 'border-glass-border/30 text-text-secondary hover:border-glass-hover'"
            @click="selectSubtitle(null)"
          >
            Desativar Legendas
          </button>
          <button
            v-for="sub in subtitles"
            :key="sub.id"
            class="focusable w-full p-2 rounded-lg text-left text-[10px] transition-smooth border"
            :class="selectedSubtitleId === sub.id ? 'border-accent text-accent font-bold bg-accent/5' : 'border-glass-border/30 text-text-secondary hover:border-glass-hover'"
            @click="selectSubtitle(sub)"
          >
            {{ sub.label }}
          </button>
          <div v-if="subtitles.length === 0" class="text-center py-4 text-[10px] text-text-muted">
            Nenhuma legenda encontrada.
          </div>
        </div>

        <!-- Progress Bar -->
        <div class="relative w-full h-1.5 bg-white/10 rounded-full cursor-pointer group" @click="seekTo">
          <div class="h-full bg-accent rounded-full progress-bar-glow relative" :style="{ width: `${progress}%` }">
            <span class="absolute right-0 top-1/2 -translate-y-1/2 w-3.5 h-3.5 bg-white rounded-full shadow-md scale-0 group-hover:scale-100 transition-smooth"></span>
          </div>
        </div>

        <!-- Actions Row -->
        <div class="flex items-center justify-between text-[11px]">
          <div class="flex items-center gap-3">
            <span class="text-text-muted font-mono text-[10px]">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
            <div class="flex items-center gap-1.5">
              <svg class="w-3.5 h-3.5 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15.536 8.464a5 5 0 010 7.072M12 6.253v11.494M8.464 8.464a5 5 0 000 7.072M19.07 4.93a10 10 0 010 14.142M4.93 4.93a10 10 0 000 14.142"/></svg>
              <input type="range" min="0" max="100" v-model="volume" @input="updateVolume" class="w-14 h-1 rounded-lg bg-white/10 accent-accent cursor-pointer" />
            </div>
          </div>

          <div class="flex items-center gap-2">
            <!-- Swap Sources trigger -->
            <button 
              @click="toggleSourceMenu"
              class="focusable px-3 py-1.5 glass-card text-text-secondary hover:text-white rounded-lg text-[10px] font-semibold transition-fast flex items-center gap-1.5"
            >
              <svg class="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16"/></svg>
              Fontes
            </button>

            <!-- Subtitle selector trigger -->
            <button 
              @click="toggleSubtitleMenu"
              class="focusable px-3 py-1.5 glass-card text-text-secondary hover:text-white rounded-lg text-[10px] font-semibold transition-fast flex items-center gap-1.5"
            >
              🍿 Legendas
            </button>
            <button class="focusable px-3 py-1.5 glass-card text-text-secondary hover:text-white rounded-lg text-[10px] font-semibold transition-fast">Áudio</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';

const props = defineProps({
  stream: { type: Object, default: null },
  title: { type: String, default: '' },
  episodeLabel: { type: String, default: '' },
  streams: { type: Array, default: () => [] },
  subtitles: { type: Array, default: () => [] }
});

const emit = defineEmits(['exit', 'progress', 'change-stream']);

const videoEl = ref(null);
const isPlaying = ref(true);
const progress = ref(0);
const currentTime = ref(0);
const duration = ref(0);
const volume = ref(80);
const showControls = ref(true);
let controlsTimer = null;

// Local active stream state to allow source switching inside the player
const activeStream = ref(props.stream);

// Overlays state
const showSourceMenu = ref(false);
const showSubtitleMenu = ref(false);
const selectedSubtitleId = ref(null);

watch(() => props.stream, (newVal) => {
  if (newVal) {
    activeStream.value = newVal;
  }
});

const togglePlay = () => {
  if (!videoEl.value) return;
  if (videoEl.value.paused) {
    videoEl.value.play();
    isPlaying.value = true;
  } else {
    videoEl.value.pause();
    isPlaying.value = false;
  }
};

const toggleControls = () => {
  showControls.value = !showControls.value;
  if (showControls.value) resetControlsTimer();
};

const resetControlsTimer = () => {
  if (controlsTimer) clearTimeout(controlsTimer);
  controlsTimer = setTimeout(() => {
    if (isPlaying.value && !showSourceMenu.value && !showSubtitleMenu.value) {
      showControls.value = false;
    }
  }, 4000);
};

let lastEmitTime = 0;

const onTimeUpdate = (e) => {
  currentTime.value = e.target.currentTime;
  duration.value = e.target.duration || 0;
  progress.value = duration.value > 0 ? (currentTime.value / duration.value) * 100 : 0;
  
  if (Date.now() - lastEmitTime > 4000) {
    lastEmitTime = Date.now();
    emit('progress', {
      currentTime: currentTime.value,
      duration: duration.value,
      progress: progress.value
    });
  }
};

const seek = (seconds) => {
  if (videoEl.value) videoEl.value.currentTime += seconds;
};

const seekTo = (e) => {
  if (!videoEl.value || !duration.value) return;
  const rect = e.currentTarget.getBoundingClientRect();
  const pct = (e.clientX - rect.left) / rect.width;
  videoEl.value.currentTime = pct * duration.value;
};

const updateVolume = () => {
  if (videoEl.value) {
    videoEl.value.volume = volume.value / 100;
  }
};

const formatTime = (s) => {
  if (!s || isNaN(s)) return '00:00';
  const m = Math.floor(s / 60);
  const sec = Math.floor(s % 60);
  return `${String(m).padStart(2, '0')}:${String(sec).padStart(2, '0')}`;
};

// Menu controls
const toggleSourceMenu = () => {
  showSourceMenu.value = !showSourceMenu.value;
  showSubtitleMenu.value = false;
  resetControlsTimer();
};

const toggleSubtitleMenu = () => {
  showSubtitleMenu.value = !showSubtitleMenu.value;
  showSourceMenu.value = false;
  resetControlsTimer();
};

const selectStream = (src) => {
  activeStream.value = src;
  showSourceMenu.value = false;
  emit('change-stream', src);
  isPlaying.value = true;
};

const selectSubtitle = (sub) => {
  selectedSubtitleId.value = sub ? sub.id : null;
  showSubtitleMenu.value = false;

  if (!videoEl.value) return;
  const tracks = videoEl.value.textTracks;
  for (let i = 0; i < tracks.length; i++) {
    if (sub && tracks[i].label === sub.label) {
      tracks[i].mode = 'showing';
    } else {
      tracks[i].mode = 'disabled';
    }
  }
};

onMounted(() => {
  resetControlsTimer();
  if (videoEl.value) {
    videoEl.value.volume = volume.value / 100;
  }
});

onUnmounted(() => {
  if (controlsTimer) clearTimeout(controlsTimer);
});
</script>

<style scoped>
.progress-bar-glow {
  box-shadow: 0 0 10px var(--accent-color, #E50914);
}
</style>
