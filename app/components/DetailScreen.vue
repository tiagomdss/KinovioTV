<template>
  <div class="min-h-screen bg-cinema-deep relative animate-ios-slide-up overflow-hidden">
    <!-- Backdrop -->
    <div class="absolute inset-x-0 top-0 z-0 h-[62svh] md:h-[70vh]">
      <img v-if="show.backdrop || show.poster" :src="show.backdrop || show.poster" :alt="show.title" class="h-full w-full object-cover" />
      <div class="absolute inset-0 bg-gradient-to-t from-cinema-deep via-cinema-deep/35 to-black/20"></div>
      <div class="absolute inset-0 bg-gradient-to-r from-cinema-deep/80 via-transparent to-transparent"></div>
      <div class="absolute inset-0 bg-gradient-to-tr from-accent/5 via-transparent to-brand/5 pointer-events-none"></div>
    </div>

    <div class="relative z-10 p-5 pt-[38svh] sm:p-8 sm:pt-[42svh] lg:p-14 lg:pt-[46vh] flex flex-col gap-8">
      <!-- Back + Header -->
      <div class="flex items-center gap-4">
        <button 
          id="btn-detail-back"
          class="focusable p-2.5 rounded-xl glass-card text-text-secondary hover:text-white transition-smooth"
          @click="$emit('back')"
        >
          <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
        </button>
        <div>
          <span class="text-[10px] text-accent font-bold uppercase tracking-wider">KinovioTV</span>
          <h2 class="text-2xl sm:text-4xl lg:text-5xl font-black text-white tracking-tight text-balance">{{ show.title }}</h2>
        </div>
      </div>

      <!-- Content Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 lg:gap-8">
        <!-- Left: Episodes + Description -->
        <div class="lg:col-span-2 flex flex-col gap-6">
          
          <!-- Episodes List -->
          <div v-if="show.episodes && show.episodes.length" class="flex flex-col gap-3.5">
            <!-- Header with Season Selector & Dubbed Mode Toggle -->
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 px-1 border-b border-glass-border/30 pb-3">
              <span class="text-xs font-bold text-text-muted uppercase tracking-wide">Episódios</span>
              
              <!-- Dubbed / Original Version Toggle -->
              <div class="flex flex-col items-end gap-1 shrink-0">
                <div class="flex items-center gap-1 bg-glass border border-glass-border/40 rounded-xl p-1">
                  <button 
                    type="button"
                    class="focusable px-3 py-1.5 rounded-lg text-[10px] font-extrabold transition-smooth whitespace-nowrap"
                    :class="!isDubbedMode ? 'bg-accent text-white shadow-sm' : 'text-text-secondary hover:text-white'"
                    @click="isDubbedMode = false"
                  >
                    <Layers3 :size="14" /> Formato Original
                  </button>
                  <button 
                    type="button"
                    class="focusable px-3 py-1.5 rounded-lg text-[10px] font-extrabold transition-smooth whitespace-nowrap"
                    :class="isDubbedMode ? 'bg-accent text-white shadow-sm' : 'text-text-secondary hover:text-white'"
                    @click="isDubbedMode = true"
                  >
                    <ListOrdered :size="14" /> Contagem Corrida
                  </button>
                </div>
                <span class="text-[8px] text-text-muted font-bold block text-right max-w-[280px] leading-tight">
                  *Obs: Indicado para novelas turcas ou brasileiras com contagem nacional de episódios diferente.
                </span>
              </div>
            </div>

            <!-- Temporadas (Netflix style cards if posters exist, otherwise clean pill buttons) -->
            <div v-if="!isDubbedMode && seasonsList.length > 1" class="flex flex-col gap-3">
              <label class="md:hidden text-[11px] font-bold text-text-secondary" for="season-select">Temporada</label>
              <select id="season-select" class="focusable md:hidden min-h-12 rounded-xl border border-white/10 bg-cinema-elevated px-4 text-sm font-semibold text-white" :value="selectedSeason" @change="selectSeason(Number($event.target.value))">
                <option v-for="season in seasonsList" :key="season.seasonNumber" :value="season.seasonNumber">{{ season.name }} · {{ season.episodeCount || 0 }} episódios</option>
              </select>
              <!-- Case A: Season Posters are present -->
              <template v-if="showPosters">
                <div class="flex items-center justify-between px-1">
                  <span class="text-xs font-black text-white uppercase tracking-wider">Temporadas</span>
                  <span class="text-[10px] text-text-muted font-semibold uppercase tracking-wider">Pôsteres</span>
                </div>
                <!-- Seasons Horizontal Cards List -->
                <div class="hidden md:flex items-center gap-4 overflow-x-auto no-scrollbar py-4 px-2.5 w-full max-w-full">
                  <button 
                    v-for="s in seasonsList" 
                    :key="s.seasonNumber"
                    :id="`season-card-${s.seasonNumber}`"
                    class="focusable group flex flex-col gap-2 shrink-0 outline-none bg-transparent border-0 text-left"
                    @click="selectSeason(s.seasonNumber)"
                  >
                    <div 
                      class="w-24 sm:w-28 aspect-[2/3] rounded-xl overflow-hidden border transition-smooth relative"
                      :class="selectedSeason === s.seasonNumber 
                        ? 'border-accent shadow-md shadow-accent/20 scale-105' 
                        : 'border-glass-border/60 opacity-60 hover:opacity-100 hover:scale-102'"
                    >
                      <img 
                        v-if="s.poster" 
                        :src="s.poster" 
                        :alt="s.name" 
                        class="w-full h-full object-cover" 
                        loading="lazy"
                      />
                      <div v-else class="w-full h-full bg-gradient-to-tr from-cinema-deep via-[#0d0d18] to-[#16162a] flex flex-col items-center justify-center p-3 text-center">
                        <Clapperboard :size="20" class="mb-2 text-text-muted" />
                        <span class="text-[8px] font-black text-text-muted uppercase tracking-wider">{{ s.name }}</span>
                      </div>
                    </div>
                    <span 
                      class="text-[10px] font-bold tracking-tight text-center sm:text-left transition-smooth"
                      :class="selectedSeason === s.seasonNumber ? 'text-white' : 'text-text-secondary group-hover:text-white'"
                    >
                      {{ s.name }}
                    </span>
                  </button>
                </div>
              </template>

              <!-- Case B: No Season Posters -> Render clean pill buttons -->
              <template v-else>
                <div class="hidden md:flex flex-wrap items-center gap-2 py-1 w-full max-w-full">
                  <button 
                    v-for="sNum in seasons" 
                    :key="sNum"
                    :id="`season-btn-${sNum}`"
                    class="focusable px-4 py-2 rounded-xl text-xs sm:text-sm font-extrabold border transition-smooth whitespace-nowrap"
                    :class="selectedSeason === sNum ? 'bg-accent text-white border-accent shadow-md shadow-accent/15' : 'bg-glass/10 text-text-secondary hover:bg-glass/30 hover:text-white border border-glass-border/40'"
                    @click="selectSeason(sNum)"
                  >
                    {{ sNum === 0 ? 'Especiais' : `Temporada ${sNum}` }}
                  </button>
                </div>
              </template>
            </div>

            <!-- Header of Current Season/Episodes list -->
            <div class="flex items-center justify-between px-1 mt-2">
              <h3 class="text-xs font-black text-white uppercase tracking-wider">
                {{ isDubbedMode ? 'Episódios (Contagem Corrida / Novelas)' : (seasonsList.find(s => s.seasonNumber === selectedSeason)?.name || `Temporada ${selectedSeason}`) }}
              </h3>
            </div>

            <!-- Carousel of Filtered Episodes Wrapper -->
            <div class="relative group/carousel w-full">
              <!-- Left Scroll Button -->
              <button 
                v-if="hasMouse && displayedEpisodes.length > 0"
                class="absolute left-2 top-1/2 -translate-y-1/2 z-30 w-10 h-10 rounded-full bg-black/60 hover:bg-accent border border-glass-border/40 text-white flex items-center justify-center transition-smooth opacity-0 group-hover/carousel:opacity-100 shadow-lg shadow-black/30 hover:scale-110"
                @click="scrollCarousel('left')"
                title="Anterior"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
              </button>

              <!-- Carousel Component -->
              <div ref="carouselRef" class="carousel-snap" style="gap: 0.75rem; padding: 0.5rem 0.25rem;">
                <div 
                  v-for="(ep, idx) in displayedEpisodes" 
                  :key="ep.id"
                  :id="`ep-card-${String(ep.id).replace(/:/g, '-')}`"
                  class="carousel-item focusable w-64 sm:w-72 rounded-xl glass-card overflow-hidden cursor-pointer transition-smooth hover:border-accent/30 group relative"
                  :class="show.episodes[selectedEpisode]?.id === ep.id ? 'border-accent/40 shadow-sm shadow-accent/10' : ''"
                  @click="selectEpisode(ep)"
                >
                  <div class="relative h-32 w-full overflow-hidden">
                    <img v-if="ep.thumbnail || show.backdrop" :src="ep.thumbnail || show.backdrop" :alt="ep.title" class="w-full h-full object-cover transition-all duration-300 group-hover:scale-[1.035]" loading="lazy" />
                    <div v-else class="flex h-full w-full items-center justify-center bg-cinema-surface text-text-muted"><Clapperboard :size="28" /></div>
                    
                    <!-- Em breve / Soon Badge -->
                    <div v-if="ep.released === false" class="absolute inset-0 bg-black/40 flex items-center justify-center pointer-events-none select-none z-10">
                      <span class="px-2.5 py-1 rounded bg-red-600 text-[8px] font-black text-white uppercase tracking-widest shadow-md">
                        {{ subtitleLanguage === 'en' ? 'Soon' : 'Em breve' }}
                      </span>
                    </div>

                    <div v-if="ep.watched" class="absolute top-2 right-2 w-5 h-5 rounded-full bg-emerald-500 flex items-center justify-center z-15">
                      <svg class="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>
                    </div>
                    <div v-if="ep.progress > 0 && !ep.watched" class="absolute bottom-0 left-0 w-full h-1 bg-white/10 z-15">
                      <div class="h-full bg-accent progress-bar-glow" :style="{ width: `${ep.progress}%` }"></div>
                    </div>
                  </div>
                  <div class="p-3">
                    <div class="flex items-center justify-between text-[9px] text-text-muted font-bold mb-0.5">
                      <span>{{ isDubbedMode ? `Nº Absoluto ${idx + 1}` : `T${ep.season} · E${ep.episode}` }} · {{ ep.duration }}</span>
                      <span v-if="ep.airDate" class="text-text-muted/70 font-semibold text-[8px]">{{ formatAirDate(ep.airDate) }}</span>
                    </div>
                    <h4 class="text-xs font-semibold text-white truncate">
                      {{ isDubbedMode ? `Episódio ${idx + 1}` : ep.title }}
                    </h4>
                  </div>
                </div>
              </div>

              <!-- Right Scroll Button -->
              <button 
                v-if="hasMouse && displayedEpisodes.length > 0"
                class="absolute right-2 top-1/2 -translate-y-1/2 z-30 w-10 h-10 rounded-full bg-black/60 hover:bg-accent border border-glass-border/40 text-white flex items-center justify-center transition-smooth opacity-0 group-hover/carousel:opacity-100 shadow-lg shadow-black/30 hover:scale-110"
                @click="scrollCarousel('right')"
                title="Próximo"
              >
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"/></svg>
              </button>
            </div>
          </div>

          <!-- Description -->
          <div class="order-first glass-card p-5 sm:p-6 rounded-2xl flex flex-col gap-3">
            <div class="flex items-center justify-between">
              <span class="text-[10px] font-bold text-accent uppercase tracking-wider">
                {{ show.episodes ? `Episódio ${selectedEpisode + 1}` : 'Sinopse' }}
              </span>
              <span class="text-[10px] text-text-muted">{{ show.year }}</span>
            </div>
            
            <h3 class="text-base font-bold text-white">
              {{ show.episodes ? show.episodes[selectedEpisode]?.title : show.title }}
            </h3>

            <!-- TMDB Rich Metadata Badges (Country, Spoken Languages) -->
            <div class="flex flex-wrap gap-2 text-[10px] font-semibold text-text-secondary mt-1">
              <span v-if="show.country" class="px-2 py-0.5 rounded bg-glass border border-glass-border flex items-center gap-1">
                <MapPin :size="13" /> {{ show.country }}
              </span>
              <span v-if="show.languages" class="px-2 py-0.5 rounded bg-glass border border-glass-border flex items-center gap-1">
                <Languages :size="13" /> {{ show.languages }}
              </span>
            </div>

            <!-- Premium iOS Play Button + Favorite -->
            <div class="flex items-center gap-2.5 mt-2 mb-1">
              <button 
                id="btn-detail-play"
                class="focusable min-h-12 px-6 bg-white hover:bg-white/90 text-black text-sm font-bold rounded-xl flex items-center gap-2 transition-smooth active:scale-[0.98] shadow-xl"
                @click="$emit('play-default', selectedEpisode)"
              >
                <svg class="w-3.5 h-3.5" fill="currentColor" viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg>
                Assistir {{ show.episodes && show.episodes[selectedEpisode]
                  ? (isDubbedMode 
                      ? `Episódio ${displayedEpisodes.findIndex(e => e.id === show.episodes[selectedEpisode].id) + 1}`
                      : `Temp. ${show.episodes[selectedEpisode].season} · Ep. ${show.episodes[selectedEpisode].episode}`)
                  : '' }}
              </button>

              <button 
                class="focusable p-2.5 rounded-xl bg-glass border border-glass-border hover:bg-glass-hover text-white transition-smooth"
                @click="$emit('favorite', show)"
                :title="isFavorite ? 'Remover da minha lista' : 'Adicionar à minha lista'"
              >
                <svg class="w-4 h-4 text-rose-500 transition-smooth" :fill="isFavorite ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </button>
            </div>

            <p class="text-xs text-text-secondary leading-relaxed mt-1">
              {{ show.episodes ? show.episodes[selectedEpisode]?.description : show.description }}
            </p>

            <div class="flex items-center gap-2 mt-1 text-[10px] text-text-muted">
              <span v-if="show.genre">{{ show.genre }}</span>
              <span v-if="show.duration" class="w-1 h-1 rounded-full bg-text-muted"></span>
              <span v-if="show.duration">{{ show.duration }}</span>
            </div>
          </div>

          <!-- TMDB Watch Providers (Onde Assistir Oficialmente) -->
          <div v-if="show.watchProviders && show.watchProviders.length > 0" class="glass-card p-5 rounded-2xl flex flex-col gap-3">
            <h4 class="text-xs font-black text-text-secondary uppercase tracking-widest flex items-center gap-2">
              <Tv :size="16" />
              Onde Assistir Oficialmente
            </h4>
            <div class="flex flex-wrap gap-3.5">
              <div 
                v-for="provider in show.watchProviders" 
                :key="provider.id"
                class="flex flex-col items-center gap-1"
                :title="provider.name"
              >
                <div class="w-11 h-11 rounded-xl overflow-hidden border border-glass-border shadow-md transition-smooth hover:scale-105">
                  <img :src="provider.logo" :alt="provider.name" class="w-full h-full object-cover" />
                </div>
                <span class="text-[8px] text-text-muted text-center max-w-[50px] truncate leading-tight font-medium">{{ provider.name }}</span>
              </div>
            </div>
            <p class="text-[9px] text-text-muted mt-1 leading-relaxed">
              * Licenciado oficialmente nas plataformas acima no Brasil. Você pode reproduzir diretamente pelo KinovioTV clicando no botão Assistir.
            </p>
          </div>
        </div>

        <!-- Right: Elenco & Personagens -->
        <div class="glass-card p-5 rounded-2xl flex flex-col gap-4 self-start lg:sticky lg:top-4 w-full">
          <h4 class="text-xs font-black text-text-muted uppercase tracking-widest">Elenco & Personagens</h4>

          <!-- Cast List -->
          <div class="flex flex-col gap-3 max-h-[60vh] overflow-y-auto no-scrollbar pr-0.5">
            <div 
              v-for="(actor, idx) in show.cast || []" 
              :key="idx" 
              class="flex items-center gap-3 p-2 rounded-xl bg-glass border border-glass-border/30 hover:border-glass-hover transition-smooth"
            >
              <!-- Actor Photo -->
              <div class="w-10 h-10 rounded-full overflow-hidden bg-cinema-elevated flex items-center justify-center border border-glass-border relative shrink-0">
                <img 
                  v-if="actor.profilePath" 
                  :src="actor.profilePath" 
                  :alt="actor.name" 
                  class="w-full h-full object-cover z-10"
                  @error="actor.profilePath = ''"
                />
                <span class="absolute inset-0 flex items-center justify-center text-[10px] font-black text-text-secondary bg-cinema-elevated uppercase">
                  {{ actor.name.split(' ').map(n => n[0]).slice(0,2).join('') }}
                </span>
              </div>

              <!-- Actor Name & Character -->
              <div class="min-w-0 flex-1">
                <h4 class="text-xs font-bold text-white truncate leading-tight">{{ actor.name }}</h4>
                <p class="text-[9px] text-text-secondary truncate mt-0.5">{{ actor.character }}</p>
              </div>
            </div>
            
            <div v-if="!show.cast || show.cast.length === 0" class="text-center py-12 text-xs text-text-muted flex flex-col items-center gap-2">
              <Users :size="28" />
              <p>Nenhum elenco disponível.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import { Clapperboard, Languages, Layers3, ListOrdered, MapPin, Tv, Users } from 'lucide-vue-next';

const props = defineProps({
  show: { type: Object, required: true },
  streams: { type: Array, default: () => [] },
  isFavorite: { type: Boolean, default: false },
  seasonViewMode: { type: String, default: 'posters' },
  subtitleLanguage: { type: String, default: 'pt-BR' }
});

const emit = defineEmits(['back', 'play', 'play-default', 'favorite', 'change-season', 'toast']);

const selectedEpisode = ref(0);
const selectedFormat = ref('4K');
const selectedSource = ref(0);
const selectedSeason = ref(1);
const customEpisodeNum = ref('');
const isDubbedMode = ref(false);

const playCustomEpisode = () => {
  const epNum = parseInt(customEpisodeNum.value);
  if (isNaN(epNum) || epNum <= 0) return;
  emit('play-default', { season: selectedSeason.value, episode: epNum });
};

const carouselRef = ref(null);
const hasMouse = ref(false);

onMounted(() => {
  // Detect if pointer-fine is supported (mouse/trackpad present)
  hasMouse.value = window.matchMedia('(pointer: fine)').matches;

  // Additional dynamic detection on mouse move
  const detectMouse = () => {
    hasMouse.value = true;
    window.removeEventListener('mousemove', detectMouse);
  };
  window.addEventListener('mousemove', detectMouse);
});

const scrollCarousel = (direction) => {
  const container = carouselRef.value;
  if (!container) return;
  const scrollAmount = container.clientWidth * 0.75;
  container.scrollBy({
    left: direction === 'left' ? -scrollAmount : scrollAmount,
    behavior: 'smooth'
  });
};

// Unique seasons computed property (coalescing to allow season 0)
const seasons = computed(() => {
  if (!props.show.episodes) return [];
  const uniqSeasons = [...new Set(props.show.episodes.map(ep => ep.season ?? 1))];
  return uniqSeasons.sort((a, b) => {
    if (a === 0) return 1;
    if (b === 0) return -1;
    return a - b;
  });
});

const hasSeasonPosters = computed(() => {
  if (!props.show.tmdbSeasons || props.show.tmdbSeasons.length === 0) return false;
  // Se pelo menos uma temporada tiver poster válido que seja diferente do poster geral
  return props.show.tmdbSeasons.some(s => s.poster && s.poster !== props.show.poster);
});

const showPosters = computed(() => {
  return hasSeasonPosters.value && props.seasonViewMode === 'posters';
});

const subtitleLanguage = ref('pt-BR');

onMounted(() => {
  if (typeof window !== 'undefined') {
    subtitleLanguage.value = localStorage.getItem('ktv_subtitle_language') || 'pt-BR';
  }
});

const formatAirDate = (dateStr) => {
  if (!dateStr) return '';
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr;
    const options = { day: '2-digit', month: 'short', year: 'numeric' };
    return date.toLocaleDateString(subtitleLanguage.value === 'en' ? 'en-US' : 'pt-BR', options);
  } catch (e) {
    return dateStr;
  }
};

const seasonsList = computed(() => {
  if (props.show.tmdbSeasons && props.show.tmdbSeasons.length > 0) {
    const availableSeasonNums = new Set(props.show.episodes ? props.show.episodes.map(ep => ep.season) : [1]);
    const list = props.show.tmdbSeasons
      .filter(s => availableSeasonNums.has(s.seasonNumber))
      .map(s => ({
        seasonNumber: s.seasonNumber,
        name: s.seasonNumber === 0 ? 'Especiais' : s.name,
        poster: s.poster || props.show.poster || '',
        episodeCount: s.episodeCount
      }));
      
    return list.sort((a, b) => {
      if (a.seasonNumber === 0) return 1;
      if (b.seasonNumber === 0) return -1;
      return a.seasonNumber - b.seasonNumber;
    });
  }
  
  return seasons.value.map(sNum => ({
    seasonNumber: sNum,
    name: sNum === 0 ? 'Especiais' : `Temporada ${sNum}`,
    poster: props.show.poster || '',
    episodeCount: props.show.episodes ? props.show.episodes.filter(e => e.season === sNum).length : 0
  }));
});

// Filtered episodes for original view or absolute list for dubbed view
const displayedEpisodes = computed(() => {
  if (!props.show.episodes) return [];
  if (isDubbedMode.value) {
    return props.show.episodes;
  }
  return props.show.episodes.filter(ep => (ep.season ?? 1) === selectedSeason.value);
});

// Filtered episodes for the active season (backwards compatibility if needed)
const filteredEpisodes = computed(() => {
  if (!props.show.episodes) return [];
  return props.show.episodes.filter(ep => (ep.season ?? 1) === selectedSeason.value);
});

// Watch to set initial season on show change
watch(() => props.show, (newShow) => {
  if (newShow?.episodes && newShow.episodes.length > 0) {
    const minSeason = Math.min(...newShow.episodes.map(e => e.season ?? 1));
    selectedSeason.value = minSeason || 1;
    selectedEpisode.value = 0;
    isDubbedMode.value = false; // Reset to original mode
  }
}, { immediate: true });

const selectSeason = (seasonNum) => {
  selectedSeason.value = seasonNum;
  emit('change-season', seasonNum);
  const idx = props.show.episodes.findIndex(ep => (ep.season ?? 1) === seasonNum);
  if (idx !== -1) {
    selectedEpisode.value = idx;
  }
};

const selectEpisode = (ep) => {
  const idx = props.show.episodes.findIndex(e => e.id === ep.id);
  if (idx !== -1) {
    selectedEpisode.value = idx;
  }
  if (ep.released === false) {
    const formatted = ep.airDate ? formatAirDate(ep.airDate) : '';
    emit('toast', {
      message: subtitleLanguage.value === 'en' 
        ? `This episode has not premiered yet. Premiere: ${formatted || 'Soon'}.`
        : `Este episódio ainda não estreou. Estreia em: ${formatted || 'Em breve'}.`,
      type: 'error'
    });
  }
};

const filteredStreams = computed(() => {
  return props.streams.filter(s => s.quality === selectedFormat.value);
});
</script>
