<template>
  <section
    ref="playerRoot"
    class="fixed inset-0 z-[100] flex items-center justify-center overflow-hidden bg-black"
    tabindex="0"
    aria-label="Player de vídeo"
    @mousemove="revealControls"
    @touchstart.passive="revealControls"
    @keydown="onKeydown"
  >
    <video
      v-if="activeStream && playableUrl"
      ref="videoEl"
      autoplay
      playsinline
      preload="auto"
      class="h-full w-full object-contain"
      @click="toggleControls"
      @loadstart="onLoadStart"
      @loadedmetadata="onLoadedMetadata"
      @canplay="onCanPlay"
      @playing="onPlaying"
      @pause="isPlaying = false"
      @waiting="isBuffering = true"
      @stalled="isBuffering = true"
      @timeupdate="onTimeUpdate"
      @ended="onEnded"
      @error="onPlaybackError"
    >
      <track
        v-for="sub in subtitles"
        :key="sub.id"
        :src="sub.url"
        :srclang="sub.lang"
        :label="sub.label"
        kind="subtitles"
      />
    </video>

    <div v-else class="absolute inset-0 flex flex-col items-center justify-center gap-4 px-8 text-center">
      <CircleOff :size="42" class="text-text-muted" />
      <div>
        <h2 class="text-lg font-bold text-white">Esta fonte não pode ser reproduzida aqui</h2>
        <p class="mt-2 max-w-md text-sm text-text-secondary">Escolha outra fonte ou volte aos detalhes. Links torrent precisam de um serviço compatível antes de chegar ao player.</p>
      </div>
      <div class="flex gap-3">
        <button class="player-button" @click="$emit('exit')"><ArrowLeft :size="18" /> Voltar</button>
        <button v-if="streams.length > 1" class="player-button player-button-primary" @click="openMenu('sources')"><ListVideo :size="18" /> Ver fontes</button>
      </div>
    </div>

    <div v-if="isBuffering && !playbackError" class="pointer-events-none absolute inset-0 flex flex-col items-center justify-center gap-4 bg-black/20">
      <div class="kinovio-loader" aria-label="Preparando reprodução"><i></i><i></i><i></i></div>
      <p class="text-xs font-semibold text-white/75">{{ hasStarted ? 'Ajustando a reprodução' : 'Preparando reprodução' }}</p>
    </div>

    <div v-if="playbackError" class="absolute inset-0 z-20 flex items-center justify-center bg-black/85 px-6">
      <div class="w-full max-w-md rounded-2xl border border-white/10 bg-cinema-elevated/95 p-6 text-center shadow-2xl">
        <CircleAlert :size="38" class="mx-auto text-amber-400" />
        <h2 class="mt-4 text-lg font-bold text-white">Não foi possível reproduzir esta fonte</h2>
        <p class="mt-2 text-sm leading-relaxed text-text-secondary">{{ playbackError }}</p>
        <div class="mt-6 grid gap-2 sm:grid-cols-2">
          <button class="player-button player-button-primary" @click="retryCurrentSource"><RefreshCw :size="18" /> Tentar novamente</button>
          <button v-if="streams.length > 1" class="player-button" @click="openMenu('sources')"><ListVideo :size="18" /> Escolher fonte</button>
          <button class="player-button" @click="$emit('exit')"><ArrowLeft :size="18" /> Voltar aos detalhes</button>
          <button v-if="hasAlternativeSource" class="player-button player-button-primary" @click="tryNextSource"><RefreshCw :size="18" /> Tentar outra fonte</button>
        </div>
      </div>
    </div>

    <div
      class="absolute inset-0 z-10 flex flex-col justify-between bg-gradient-to-t from-black/90 via-transparent to-black/75 p-4 transition-opacity duration-200 sm:p-6"
      :class="showControls && !playbackError ? 'opacity-100' : 'pointer-events-none opacity-0'"
      @click.self="toggleControls"
    >
      <header class="flex items-start justify-between gap-4 safe-top">
        <div class="flex min-w-0 items-center gap-3">
          <button id="btn-player-back" class="player-icon-button focusable" aria-label="Voltar aos detalhes" @click="$emit('exit')"><ArrowLeft :size="21" /></button>
          <div class="min-w-0">
            <h1 class="truncate text-sm font-bold text-white sm:text-base">{{ title }}</h1>
            <p class="mt-0.5 truncate text-[11px] text-white/55">{{ episodeLabel || cleanSourceName }}</p>
          </div>
        </div>
        <div class="hidden items-center gap-2 rounded-xl border border-white/10 bg-black/30 px-3 py-2 text-[10px] font-bold text-white/70 backdrop-blur-xl sm:flex">
          <span class="text-accent">{{ activeStream?.quality || 'AUTO' }}</span>
          <span v-if="activeStream?.type">{{ activeStream.type }}</span>
          <span v-if="activeStream?.size">{{ activeStream.size }}</span>
        </div>
      </header>

      <div class="flex items-center justify-center gap-5 sm:gap-8">
        <button class="player-center-button focusable" aria-label="Voltar 10 segundos" @click="seek(-10)"><RotateCcw :size="25" /><span>10</span></button>
        <button id="btn-player-toggle" class="focusable flex h-16 w-16 items-center justify-center rounded-full bg-white text-black shadow-2xl transition-transform active:scale-95 sm:h-20 sm:w-20" :aria-label="isPlaying ? 'Pausar' : 'Reproduzir'" @click="togglePlay">
          <Pause v-if="isPlaying" :size="30" fill="currentColor" />
          <Play v-else :size="32" fill="currentColor" class="ml-1" />
        </button>
        <button class="player-center-button focusable" aria-label="Avançar 10 segundos" @click="seek(10)"><RotateCw :size="25" /><span>10</span></button>
      </div>

      <footer class="mx-auto w-full max-w-6xl safe-bottom-player">
        <div class="rounded-2xl border border-white/10 bg-black/35 p-3 backdrop-blur-2xl sm:p-4">
          <div class="flex items-center gap-3">
            <span class="w-11 text-right text-[10px] font-semibold tabular-nums text-white/65">{{ formatTime(currentTime) }}</span>
            <input v-model.number="scrubValue" class="player-progress min-w-0 flex-1" type="range" min="0" max="100" step="0.1" aria-label="Progresso do vídeo" @input="previewSeek" @change="commitSeek" />
            <span class="w-11 text-[10px] font-semibold tabular-nums text-white/65">{{ formatTime(duration) }}</span>
          </div>

          <div class="mt-3 flex items-center justify-between gap-2">
            <div class="flex items-center gap-1">
              <button class="player-icon-button focusable" :aria-label="muted ? 'Ativar som' : 'Silenciar'" @click="toggleMute">
                <VolumeX v-if="muted || volume === 0" :size="19" />
                <Volume2 v-else :size="19" />
              </button>
              <input v-model.number="volume" class="player-volume hidden sm:block" type="range" min="0" max="100" aria-label="Volume" @input="updateVolume" />
            </div>

            <div class="flex items-center gap-1 sm:gap-2">
              <button v-if="streams.length > 1" class="player-action focusable" @click="openMenu('sources')"><ListVideo :size="18" /><span class="hidden sm:inline">Fontes</span></button>
              <button class="player-action focusable" @click="openMenu('subtitles')"><Captions :size="18" /><span class="hidden sm:inline">Legendas</span></button>
              <button class="player-action focusable" @click="toggleFullscreen"><Maximize :size="18" /><span class="hidden md:inline">Tela cheia</span></button>
            </div>
          </div>
        </div>
      </footer>
    </div>

    <div v-if="activeMenu" class="absolute inset-0 z-30 flex items-end bg-black/55 backdrop-blur-sm sm:items-center sm:justify-center" @click.self="activeMenu = null">
      <div class="max-h-[72vh] w-full overflow-y-auto rounded-t-2xl border border-white/10 bg-cinema-elevated p-5 shadow-2xl sm:max-w-lg sm:rounded-2xl">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-[10px] font-bold uppercase tracking-[0.16em] text-accent">Reprodução</p>
            <h2 class="mt-1 text-lg font-bold text-white">{{ activeMenu === 'sources' ? 'Escolher fonte' : 'Legendas' }}</h2>
          </div>
          <button class="player-icon-button focusable" aria-label="Fechar menu" @click="activeMenu = null"><X :size="20" /></button>
        </div>

        <div v-if="activeMenu === 'sources'" class="mt-5 grid gap-2">
          <button v-for="(source, index) in playableStreams" :key="source.url || index" class="focusable flex min-h-14 items-center gap-3 rounded-xl border p-3 text-left" :class="activeStream === source ? 'border-accent/60 bg-accent/10' : 'border-white/8 bg-white/[0.03] hover:bg-white/[0.06]'" @click="selectStream(source)">
            <span class="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg bg-white/[0.06] text-text-secondary"><Play :size="17" /></span>
            <span class="min-w-0 flex-1"><strong class="block truncate text-xs text-white">{{ source.name || `Fonte ${index + 1}` }}</strong><small class="mt-1 block truncate text-[10px] text-text-secondary">{{ [source.quality, source.type, source.size].filter(Boolean).join(' · ') || 'Qualidade automática' }}</small></span>
            <Check v-if="activeStream === source" :size="18" class="text-accent" />
          </button>
        </div>

        <div v-else class="mt-5 grid gap-2">
          <button class="menu-option" :class="!selectedSubtitleId ? 'menu-option-active' : ''" @click="selectSubtitle(null)"><CaptionsOff :size="18" /> Desativadas <Check v-if="!selectedSubtitleId" :size="18" class="ml-auto" /></button>
          <button v-for="sub in subtitles" :key="sub.id" class="menu-option" :class="selectedSubtitleId === sub.id ? 'menu-option-active' : ''" @click="selectSubtitle(sub)"><Languages :size="18" /> {{ sub.label }} <Check v-if="selectedSubtitleId === sub.id" :size="18" class="ml-auto" /></button>
          <p v-if="subtitles.length === 0" class="py-8 text-center text-sm text-text-secondary">Nenhuma legenda foi retornada pelas fontes conectadas.</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import { ArrowLeft, Captions, CaptionsOff, Check, CircleAlert, CircleOff, Languages, ListVideo, Maximize, Pause, Play, RefreshCw, RotateCcw, RotateCw, Volume2, VolumeX, X } from 'lucide-vue-next';

const props = defineProps({
  stream: { type: Object, default: null },
  title: { type: String, default: '' },
  episodeLabel: { type: String, default: '' },
  streams: { type: Array, default: () => [] },
  subtitles: { type: Array, default: () => [] },
});
const emit = defineEmits(['exit', 'progress', 'change-stream']);

const playerRoot = ref(null);
const videoEl = ref(null);
const activeStream = ref(props.stream);
const isPlaying = ref(false);
const isBuffering = ref(true);
const hasStarted = ref(false);
const playbackError = ref('');
const currentTime = ref(0);
const duration = ref(0);
const scrubValue = ref(0);
const volume = ref(80);
const muted = ref(false);
const showControls = ref(true);
const activeMenu = ref(null);
const selectedSubtitleId = ref(null);
const attemptedUrls = ref(new Set());
let controlsTimer;
let sourceTimer;
let lastEmitTime = 0;
let hlsInstance = null;
let sourceLoadId = 0;
let handlingFailure = false;

const cleanUrl = (value) => typeof value === 'string' ? value.split('|')[0].trim() : '';
const playableStreams = computed(() => props.streams.filter(source => cleanUrl(source?.url)));
const playableUrl = computed(() => cleanUrl(activeStream.value?.url));
const cleanSourceName = computed(() => (activeStream.value?.name || 'Fonte automática').replace(/^\[[^\]]+\]\s*/, ''));
const hasAlternativeSource = computed(() => playableStreams.value.some(source => !attemptedUrls.value.has(cleanUrl(source.url))));

watch(() => props.stream, source => { if (source) selectStream(source, false); });

const destroyHls = () => {
  hlsInstance?.destroy();
  hlsInstance = null;
};

const clearSourceTimer = () => clearTimeout(sourceTimer);
const startSourceTimer = () => {
  clearSourceTimer();
  sourceTimer = setTimeout(() => {
    playbackError.value = 'Esta fonte demorou demais para responder. Você pode tentar novamente ou escolher outro servidor.';
    onPlaybackError();
  }, 35000);
};

const isHlsUrl = value => /\.m3u8(?:$|[?#])/i.test(value) || /\/hls\//i.test(value);

const loadActiveSource = async () => {
  const video = videoEl.value;
  const url = playableUrl.value;
  const loadId = ++sourceLoadId;
  handlingFailure = false;
  destroyHls();
  if (!video || !url) return;
  startSourceTimer();

  video.pause();
  video.removeAttribute('src');
  video.load();

  if (isHlsUrl(url) && !video.canPlayType('application/vnd.apple.mpegurl')) {
    try {
      const { default: Hls } = await import('hls.js');
      if (loadId !== sourceLoadId || !videoEl.value) return;
      if (!Hls.isSupported()) throw new Error('HLS is not supported in this browser');

      hlsInstance = new Hls({
        enableWorker: true,
        lowLatencyMode: false,
        backBufferLength: 90,
        maxBufferLength: 30,
        manifestLoadPolicy: {
          default: {
            maxTimeToFirstByteMs: 8000,
            maxLoadTimeMs: 15000,
            timeoutRetry: null,
            errorRetry: null,
          },
        },
      });
      hlsInstance.on(Hls.Events.MANIFEST_PARSED, () => {
        if (loadId === sourceLoadId) video.play().catch(() => {});
      });
      hlsInstance.on(Hls.Events.ERROR, (_event, data) => {
        if (!data.fatal || loadId !== sourceLoadId) return;
        if (data.type === Hls.ErrorTypes.NETWORK_ERROR && data.response?.code === 0) {
          playbackError.value = 'O servidor bloqueou a conexão deste navegador. Tente outra fonte ou use o aplicativo Android.';
        }
        onPlaybackError();
      });
      hlsInstance.loadSource(url);
      hlsInstance.attachMedia(video);
      return;
    } catch (error) {
      console.warn('Falha ao iniciar reprodução HLS:', error);
      onPlaybackError();
      return;
    }
  }

  video.src = url;
  video.load();
  video.play().catch(error => {
    if (error?.name !== 'NotAllowedError') onPlaybackError();
  });
};

const onLoadStart = () => { isBuffering.value = true; playbackError.value = ''; };
const onLoadedMetadata = event => { duration.value = event.target.duration || 0; };
const onCanPlay = () => { clearSourceTimer(); isBuffering.value = false; };
const onPlaying = () => { clearSourceTimer(); isPlaying.value = true; isBuffering.value = false; hasStarted.value = true; resetControlsTimer(); };
const onEnded = () => { isPlaying.value = false; showControls.value = true; };

const onPlaybackError = () => {
  if (handlingFailure) return;
  handlingFailure = true;
  clearSourceTimer();
  isPlaying.value = false;
  isBuffering.value = false;
  if (playableUrl.value) attemptedUrls.value.add(playableUrl.value);
  const next = playableStreams.value.find(source => !attemptedUrls.value.has(cleanUrl(source.url)));
  if (next) {
    selectStream(next);
    return;
  }
  if (!playbackError.value) playbackError.value = 'A fonte recusou a conexão, expirou ou usa um formato que este navegador não consegue abrir.';
  showControls.value = true;
};

const tryNextSource = () => {
  const next = playableStreams.value.find(source => !attemptedUrls.value.has(cleanUrl(source.url)));
  if (next) selectStream(next);
};

const retryCurrentSource = () => {
  if (!activeStream.value) return;
  attemptedUrls.value.delete(playableUrl.value);
  handlingFailure = false;
  playbackError.value = '';
  isBuffering.value = true;
  nextTick(loadActiveSource);
};

const selectStream = (source, notify = true) => {
  activeStream.value = source;
  playbackError.value = '';
  isBuffering.value = true;
  activeMenu.value = null;
  if (notify) emit('change-stream', source);
  nextTick(loadActiveSource);
};

const togglePlay = async () => {
  if (!videoEl.value) return;
  if (videoEl.value.paused) await videoEl.value.play().catch(onPlaybackError);
  else videoEl.value.pause();
  revealControls();
};
const seek = seconds => { if (videoEl.value) videoEl.value.currentTime = Math.max(0, Math.min(duration.value || 0, videoEl.value.currentTime + seconds)); revealControls(); };
const previewSeek = () => { revealControls(); };
const commitSeek = () => { if (videoEl.value && duration.value) videoEl.value.currentTime = (scrubValue.value / 100) * duration.value; };
const updateVolume = () => { if (videoEl.value) { videoEl.value.volume = volume.value / 100; muted.value = false; videoEl.value.muted = false; } };
const toggleMute = () => { if (!videoEl.value) return; videoEl.value.muted = !videoEl.value.muted; muted.value = videoEl.value.muted; };

const onTimeUpdate = event => {
  currentTime.value = event.target.currentTime || 0;
  duration.value = event.target.duration || 0;
  scrubValue.value = duration.value ? (currentTime.value / duration.value) * 100 : 0;
  if (Date.now() - lastEmitTime > 4000) {
    lastEmitTime = Date.now();
    emit('progress', { currentTime: currentTime.value, duration: duration.value, progress: scrubValue.value });
  }
};

const revealControls = () => { showControls.value = true; resetControlsTimer(); };
const toggleControls = () => { showControls.value = !showControls.value; if (showControls.value) resetControlsTimer(); };
const resetControlsTimer = () => { clearTimeout(controlsTimer); controlsTimer = setTimeout(() => { if (isPlaying.value && !activeMenu.value) showControls.value = false; }, 4000); };
const openMenu = menu => { activeMenu.value = menu; showControls.value = true; clearTimeout(controlsTimer); };

const selectSubtitle = sub => {
  selectedSubtitleId.value = sub?.id || null;
  activeMenu.value = null;
  if (!videoEl.value) return;
  Array.from(videoEl.value.textTracks || []).forEach(track => { track.mode = sub && track.label === sub.label ? 'showing' : 'disabled'; });
};

const toggleFullscreen = async () => {
  if (!document.fullscreenElement) await playerRoot.value?.requestFullscreen?.();
  else await document.exitFullscreen?.();
};

const onKeydown = event => {
  if (event.key === ' ' || event.key === 'Enter') { event.preventDefault(); togglePlay(); }
  else if (event.key === 'ArrowLeft') seek(-10);
  else if (event.key === 'ArrowRight') seek(10);
  else if (event.key === 'Escape') activeMenu.value ? activeMenu.value = null : emit('exit');
  else if (event.key.toLowerCase() === 'f') toggleFullscreen();
};

const formatTime = value => {
  if (!Number.isFinite(value)) return '00:00';
  const hours = Math.floor(value / 3600);
  const minutes = Math.floor((value % 3600) / 60);
  const seconds = Math.floor(value % 60);
  return hours ? `${hours}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}` : `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
};

onMounted(() => { playerRoot.value?.focus(); updateVolume(); resetControlsTimer(); nextTick(loadActiveSource); });
onUnmounted(() => { clearTimeout(controlsTimer); clearSourceTimer(); sourceLoadId += 1; destroyHls(); });
</script>

<style scoped>
.player-button { display:inline-flex; min-height:44px; align-items:center; justify-content:center; gap:.5rem; border:1px solid rgba(255,255,255,.12); border-radius:.75rem; padding:0 1rem; color:white; font-size:.8rem; font-weight:700; background:rgba(255,255,255,.06); }
.player-button-primary { border-color:rgba(122,77,255,.55); background:#7A4DFF; }
.player-icon-button { display:inline-flex; width:44px; height:44px; align-items:center; justify-content:center; border:1px solid rgba(255,255,255,.1); border-radius:.75rem; color:white; background:rgba(0,0,0,.32); backdrop-filter:blur(18px); }
.player-center-button { position:relative; display:flex; width:52px; height:52px; align-items:center; justify-content:center; border-radius:999px; color:white; background:rgba(255,255,255,.06); }
.player-center-button span { position:absolute; font-size:8px; font-weight:800; }
.player-action { display:inline-flex; min-width:44px; min-height:42px; align-items:center; justify-content:center; gap:.45rem; border-radius:.65rem; padding:0 .7rem; color:rgba(255,255,255,.74); }
.player-action:hover { color:white; background:rgba(255,255,255,.08); }
.menu-option { display:flex; min-height:52px; align-items:center; gap:.75rem; border:1px solid rgba(255,255,255,.08); border-radius:.75rem; padding:0 1rem; color:rgba(255,255,255,.72); background:rgba(255,255,255,.03); }
.menu-option-active { border-color:rgba(122,77,255,.58); color:white; background:rgba(122,77,255,.12); }
.player-progress,.player-volume { accent-color:#7A4DFF; cursor:pointer; }
.player-volume { width:72px; }
.kinovio-loader { display:flex; gap:7px; align-items:center; }
.kinovio-loader i { width:9px; height:28px; border-radius:3px; background:linear-gradient(180deg,#D94CFF,#7A4DFF); animation:loader-wave .9s ease-in-out infinite alternate; }
.kinovio-loader i:nth-child(2) { animation-delay:.15s; height:40px; }
.kinovio-loader i:nth-child(3) { animation-delay:.3s; }
@keyframes loader-wave { from { transform:scaleY(.55); opacity:.45; } to { transform:scaleY(1); opacity:1; } }
.safe-top { padding-top:env(safe-area-inset-top,0); }
.safe-bottom-player { padding-bottom:env(safe-area-inset-bottom,0); }
@media (prefers-reduced-motion:reduce) { .kinovio-loader i { animation:none; } }
</style>
