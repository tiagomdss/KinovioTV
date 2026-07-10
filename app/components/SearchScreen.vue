<template>
  <div class="p-5 sm:p-8 lg:p-14 flex flex-col gap-6 animate-fade-in">
    <!-- Search Header -->
    <div>
      <h2 class="text-2xl sm:text-3xl font-black tracking-tight text-white">Buscar</h2>
      <p class="text-xs text-text-muted mt-1">Encontre filmes, séries, animes e muito mais</p>
    </div>

    <!-- Search Input -->
    <div class="flex gap-2 max-w-xl">
      <div class="relative flex-1">
        <svg class="w-4 h-4 text-text-muted absolute left-3.5 top-1/2 -translate-y-1/2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
        <input 
          type="text" 
          v-model="query"
          @keyup.enter="performSearch(query)"
          placeholder="Pesquisar título, ator, gênero..." 
          class="w-full glass-card pl-10 pr-4 py-3 rounded-xl text-sm text-white placeholder:text-text-muted focus:outline-none focus:border-accent/40 transition-smooth"
        />
      </div>
      <button 
        id="btn-search"
        @click="performSearch(query)"
        class="focusable px-5 py-3 bg-accent hover:bg-accent-hover text-white font-bold rounded-xl transition-smooth text-sm"
      >
        Buscar
      </button>
    </div>

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
          🔍 {{ s }}
        </button>
      </div>
    </div>

    <!-- Category Filters -->
    <CategoryChips @select="$emit('filter', $event)" />

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

    <!-- Initial State -->
    <div v-else class="flex flex-col items-center justify-center py-20 gap-3">
      <svg class="w-12 h-12 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1"><path stroke-linecap="round" stroke-linejoin="round" d="M7 4V2m0 2a2 2 0 012 2v12a2 2 0 01-2 2H5a2 2 0 01-2-2V6a2 2 0 012-2h2zm0 0h14l-2 18H9l-2-18z"/></svg>
      <p class="text-sm text-text-muted">Pesquise por filmes, séries ou animes</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { isNsfwContent } from '~/utils/nsfw';

const props = defineProps({
  results: { type: Array, default: () => [] },
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

const clearRecents = () => {
  recentSearches.value = [];
  if (typeof window !== 'undefined') {
    localStorage.removeItem('ktv_recent_searches');
  }
};
</script>
