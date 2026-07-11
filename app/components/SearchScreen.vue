<template>
  <div class="p-5 sm:p-8 lg:p-14 pb-32 flex flex-col gap-7 animate-fade-in">
    <!-- Search Header -->
    <div>
      <p class="text-[10px] font-bold uppercase tracking-[0.18em] text-accent">Descoberta</p>
      <h2 class="mt-2 text-3xl sm:text-4xl font-black tracking-tight text-white">Explorar</h2>
      <p class="text-sm text-text-secondary mt-1">Encontre algo bom para assistir agora.</p>
    </div>

    <!-- Search Input -->
    <form class="flex gap-2 max-w-xl" @submit.prevent="performSearch(query)">
      <div class="relative flex-1">
        <svg class="w-4 h-4 text-text-muted absolute left-3.5 top-1/2 -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
        <input 
          type="text" 
          v-model="query"
          placeholder="Pesquisar título, ator, gênero..." 
          aria-label="Pesquisar no KinovioTV"
          class="w-full min-h-12 glass-card pl-10 pr-4 rounded-xl text-base text-white placeholder:text-text-muted focus:outline-none focus:border-accent/50 transition-smooth"
        />
      </div>
      <button 
        id="btn-search"
        type="submit"
        :disabled="!query.trim()"
        class="focusable min-h-12 px-5 bg-accent hover:bg-accent-hover disabled:opacity-40 disabled:cursor-not-allowed text-white font-bold rounded-xl transition-smooth text-sm"
      >
        <Search :size="18" />
        <span class="sr-only">Buscar</span>
      </button>
    </form>

    <!-- Buscas Recentes -->
    <div v-if="!searched && recentSearches.length > 0" class="flex flex-col gap-3 max-w-xl">
      <div class="flex items-center justify-between px-0.5">
        <span class="text-xs font-black text-white uppercase tracking-wider">Buscas Recentes</span>
        <button 
          @click="clearRecents" 
          class="focusable text-[10px] font-bold text-rose-400 hover:text-rose-300 transition-smooth"
        >
          Limpar
        </button>
      </div>
      <div class="flex flex-wrap gap-2">
        <button 
          v-for="s in recentSearches" 
          :key="s"
          class="focusable px-3.5 py-2 rounded-xl bg-glass border border-glass-border/40 hover:bg-glass-hover text-text-secondary hover:text-white text-xs font-bold transition-smooth"
          @click="selectRecent(s)"
        >
          <History :size="14" /> {{ s }}
        </button>
      </div>
    </div>

    <!-- Category Filters -->
    <CategoryChips @select="$emit('filter', $event)" />

    <!-- Discovery before the first search -->
    <template v-if="!searched && !loading">
      <section v-if="filteredSuggestions.length" class="flex flex-col gap-4">
        <div class="flex items-end justify-between">
          <div>
            <h3 class="text-lg font-bold text-white">Em alta para você</h3>
            <p class="mt-1 text-xs text-text-muted">Uma mistura das categorias que estão chamando atenção.</p>
          </div>
        </div>
        <div class="-mx-5 flex snap-x gap-3 overflow-x-auto px-5 pb-2 no-scrollbar">
          <button
            v-for="(item, index) in filteredSuggestions.slice(0, 8)"
            :key="item.id || index"
            class="focusable group w-[146px] shrink-0 snap-start text-left"
            @click="$emit('select', item)"
          >
            <span class="relative block aspect-[2/3] overflow-hidden rounded-xl border border-white/8 bg-cinema-elevated">
              <img v-if="item.poster || item.backdrop" :src="item.poster || item.backdrop" :alt="item.title" class="h-full w-full object-cover transition-transform duration-300 group-active:scale-[0.98]" loading="lazy" />
              <span class="absolute left-2 top-2 rounded-md bg-black/70 px-2 py-1 text-[9px] font-bold text-white backdrop-blur">{{ index + 1 }}</span>
            </span>
            <strong class="mt-2 block truncate text-xs text-white">{{ item.title }}</strong>
            <small class="mt-1 block truncate text-[10px] text-text-muted">{{ item.year || 'Descobrir' }}<template v-if="item.genre"> · {{ item.genre }}</template></small>
          </button>
        </div>
      </section>

      <section class="grid grid-cols-2 gap-3">
        <button v-for="idea in searchIdeas" :key="idea.query" class="focusable flex min-h-[86px] items-end overflow-hidden rounded-2xl border border-white/8 p-4 text-left transition-smooth active:scale-[0.98]" :class="idea.style" @click="selectIdea(idea.query)">
          <span><component :is="idea.icon" :size="19" class="mb-2" /><strong class="block text-sm text-white">{{ idea.label }}</strong></span>
        </button>
      </section>
    </template>

    <!-- Loading -->
    <div v-if="loading" class="flex flex-col items-center justify-center py-16 gap-3">
      <div class="w-7 h-7 border-2 border-accent border-t-transparent rounded-full animate-spin"></div>
      <span class="text-xs text-text-muted">Buscando nos addons...</span>
    </div>

    <!-- Results Grid -->
    <div v-else-if="filteredResults.length > 0" class="grid grid-cols-3 sm:grid-cols-4 lg:grid-cols-6 xl:grid-cols-8 gap-3 sm:gap-4">
      <MediaCard
        v-for="(item, i) in filteredResults"
        :key="item.id || i"
        :item="item"
        layout="poster"
        :card-id="`search-${i}`"
        @select="$emit('select', item)"
      />
    </div>

    <!-- Empty State -->
    <div v-else-if="searched || (results.length > 0 && filteredResults.length === 0)" class="flex flex-col items-center justify-center py-20 gap-3">
      <svg class="w-12 h-12 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1"><path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
      <p class="text-sm text-text-muted">Nenhum resultado encontrado</p>
      <p v-if="results.length > 0 && filteredResults.length === 0" class="text-[10px] text-rose-500 font-bold">
        🔞 Conteúdo ocultado pelo Controle Parental (+18). Habilite nas Configurações se desejar visualizar.
      </p>
      <p v-else class="text-[10px] text-text-muted">Tente outro termo ou configure addons em Configurações</p>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { Clock3, Flame, History, Laugh, Search, Sparkles } from 'lucide-vue-next';
import { isNsfwContent } from '~/utils/nsfw';

const props = defineProps({
  results: { type: Array, default: () => [] },
  suggestions: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  searched: { type: Boolean, default: false },
});

const emit = defineEmits(['search', 'filter', 'select']);

const query = ref('');
const recentSearches = ref([]);
const enableNsfw = ref(false);

const checkNsfwState = () => {
  if (typeof window !== 'undefined') {
    enableNsfw.value = localStorage.getItem('ktv_enable_nsfw') === 'true';
  }
};

onMounted(() => {
  if (typeof window !== 'undefined') {
    const saved = localStorage.getItem('ktv_recent_searches');
    if (saved) recentSearches.value = JSON.parse(saved);
  }
  checkNsfwState();
  if (typeof window !== 'undefined') {
    window.addEventListener('ktv-nsfw-updated', checkNsfwState);
  }
});

const filteredResults = computed(() => {
  if (!props.results) return [];
  if (enableNsfw.value) return props.results;
  return props.results.filter(item => !isNsfwContent(item));
});
const filteredSuggestions = computed(() => enableNsfw.value ? props.suggestions : props.suggestions.filter(item => !isNsfwContent(item)));
const searchIdeas = [
  { label: 'Filmes para esta noite', query: 'Filmes', icon: Flame, style: 'bg-gradient-to-br from-violet-950/80 to-cinema-elevated text-violet-300' },
  { label: 'Séries curtas', query: 'Séries', icon: Clock3, style: 'bg-gradient-to-br from-cyan-950/70 to-cinema-elevated text-cyan-300' },
  { label: 'Anime em destaque', query: 'Anime', icon: Sparkles, style: 'bg-gradient-to-br from-fuchsia-950/70 to-cinema-elevated text-fuchsia-300' },
  { label: 'Para dar risada', query: 'Comédia', icon: Laugh, style: 'bg-gradient-to-br from-amber-950/60 to-cinema-elevated text-amber-300' },
];

const performSearch = (q) => {
  if (!q || !q.trim()) return;
  const trimmed = q.trim();
  recentSearches.value = [trimmed, ...recentSearches.value.filter(s => s !== trimmed)].slice(0, 5);
  if (typeof window !== 'undefined') {
    localStorage.setItem('ktv_recent_searches', JSON.stringify(recentSearches.value));
  }
  emit('search', trimmed);
};

const selectRecent = (s) => {
  query.value = s;
  performSearch(s);
};

const selectIdea = value => {
  query.value = value;
  performSearch(value);
};

const clearRecents = () => {
  recentSearches.value = [];
  if (typeof window !== 'undefined') {
    localStorage.removeItem('ktv_recent_searches');
  }
};
</script>
