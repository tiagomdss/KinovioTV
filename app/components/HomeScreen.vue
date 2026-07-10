<template>
  <div class="relative min-h-screen">
    <!-- Hero Banner -->
    <HeroBanner 
      :items="heroItems"
      @play="$emit('open-detail', $event)"
      @info="$emit('open-detail', $event)"
      @favorite="handleFavorite"
    />

    <!-- Content Rows -->
    <div class="relative z-10 -mt-5 flex flex-col gap-8 sm:gap-10 px-4 sm:px-6 lg:px-12 pb-32">

      <nav class="flex gap-2 overflow-x-auto no-scrollbar -mx-4 px-4" aria-label="Categorias principais">
        <button
          v-for="category in quickCategories"
          :key="category.label"
          class="focusable inline-flex min-h-11 shrink-0 items-center gap-2 rounded-xl border border-white/10 bg-white/[0.045] px-4 text-xs font-semibold text-text-primary transition-smooth hover:border-accent/30 hover:bg-accent/10"
          @click="$emit('filter-category', category)"
        >
          <component :is="category.icon" :size="16" :stroke-width="1.8" />
          {{ category.label }}
        </button>
      </nav>

      <!-- Continue Assistindo -->
      <DragCarousel
        v-if="continueWatching.length > 0"
        title="Continue Assistindo"
        :items="continueWatching"
        layout="landscape"
        carousel-id="continue"
        @select="$emit('open-detail', $event)"
      />

      <!-- Recomendados -->
      <DragCarousel
        v-if="filteredRecommended.length > 0"
        title="Recomendados para Você"
        :items="filteredRecommended"
        layout="poster"
        carousel-id="recommended"
        @select="$emit('open-detail', $event)"
      />

      <!-- Top 10 Filmes da Semana -->
      <TopTenRow 
        v-if="topTenMovies.length > 0"
        title="Top 10 Filmes da Semana"
        :items="topTenMovies"
        @select="$emit('open-detail', $event)"
      />

      <!-- Top 10 Séries da Semana -->
      <TopTenRow 
        v-if="topTenSeries.length > 0"
        title="Top 10 Séries da Semana"
        :items="topTenSeries"
        @select="$emit('open-detail', $event)"
      />

      <!-- Filmes -->
      <DragCarousel
        title="Filmes"
        subtitle="Populares"
        :items="filteredMovies"
        :loading="loadingMovies"
        layout="poster"
        carousel-id="movies"
        @select="$emit('open-detail', $event)"
        @hover="$emit('update-backdrop', $event)"
      />

      <!-- Séries -->
      <DragCarousel
        title="Séries"
        subtitle="Trending"
        :items="filteredSeries"
        :loading="loadingSeries"
        layout="poster"
        badge="Série"
        badge-type="serie"
        carousel-id="series"
        @select="$emit('open-detail', $event)"
        @hover="$emit('update-backdrop', $event)"
      />

      <!-- Filmes de Animes -->
      <DragCarousel
        v-if="filteredAnimeMovies.length > 0"
        title="Filmes de Animes"
        :items="filteredAnimeMovies"
        layout="poster"
        badge="Anime"
        badge-type="anime"
        carousel-id="animemovies"
        @select="$emit('open-detail', $event)"
      />

      <!-- Séries de Animes -->
      <DragCarousel
        v-if="filteredAnimeSeries.length > 0"
        title="Séries de Animes"
        :items="filteredAnimeSeries"
        layout="poster"
        badge="Anime"
        badge-type="anime"
        carousel-id="animeseries"
        @select="$emit('open-detail', $event)"
      />

      <!-- Filmes K-Drama -->
      <DragCarousel
        v-if="filteredKdramaMovies.length > 0"
        title="Filmes K-Drama"
        :items="filteredKdramaMovies"
        layout="poster"
        badge="K-Drama"
        badge-type="kdrama"
        carousel-id="kdramamovies"
        @select="$emit('open-detail', $event)"
      />

      <!-- Séries K-Drama -->
      <DragCarousel
        v-if="filteredKdramaSeries.length > 0"
        title="Séries K-Drama"
        :items="filteredKdramaSeries"
        layout="poster"
        badge="K-Drama"
        badge-type="kdrama"
        carousel-id="kdramaseries"
        @select="$emit('open-detail', $event)"
      />

      <!-- Filmes BL -->
      <DragCarousel
        v-if="filteredBlMovies.length > 0"
        title="Filmes BL"
        :items="filteredBlMovies"
        layout="poster"
        badge="BL"
        badge-type="kdrama"
        carousel-id="blmovies"
        @select="$emit('open-detail', $event)"
      />

      <!-- Séries BL -->
      <DragCarousel
        v-if="filteredBlSeries.length > 0"
        title="Séries BL"
        :items="filteredBlSeries"
        layout="poster"
        badge="BL"
        badge-type="kdrama"
        carousel-id="blseries"
        @select="$emit('open-detail', $event)"
      />

      <!-- Categorias -->
      <div>
        <h2 class="text-base sm:text-lg font-bold text-text-primary tracking-tight mb-4 px-1">Explorar Categorias</h2>
        <CategoryChips @select="$emit('filter-category', $event)" />
      </div>

    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue';
import { Clapperboard, Tv, Sparkles, Heart, Shapes } from 'lucide-vue-next';
import { isNsfwContent } from '~/utils/nsfw';

const props = defineProps({
  movies: { type: Array, default: () => [] },
  series: { type: Array, default: () => [] },
  animeMovies: { type: Array, default: () => [] },
  animeSeries: { type: Array, default: () => [] },
  kdramaMovies: { type: Array, default: () => [] },
  kdramaSeries: { type: Array, default: () => [] },
  blMovies: { type: Array, default: () => [] },
  blSeries: { type: Array, default: () => [] },
  recentlyAdded: { type: Array, default: () => [] },
  recommended: { type: Array, default: () => [] },
  continueWatching: { type: Array, default: () => [] },
  loadingMovies: { type: Boolean, default: false },
  loadingSeries: { type: Boolean, default: false },
});

defineEmits(['open-detail', 'update-backdrop', 'filter-category']);

const enableNsfw = ref(false);
const quickCategories = [
  { id: 'movies', label: 'Filmes', icon: Clapperboard },
  { id: 'series', label: 'Séries', icon: Tv },
  { id: 'anime', label: 'Anime', icon: Sparkles },
  { id: 'kdrama', label: 'K-Drama', icon: Shapes },
  { id: 'bl', label: 'BL', icon: Heart },
];

const checkNsfwState = () => {
  if (typeof window !== 'undefined') {
    enableNsfw.value = localStorage.getItem('ktv_enable_nsfw') === 'true';
  }
};

onMounted(() => {
  checkNsfwState();
  if (typeof window !== 'undefined') {
    window.addEventListener('ktv-nsfw-updated', checkNsfwState);
  }
});

const filterNsfw = (list) => {
  if (!list) return [];
  if (enableNsfw.value) return list;
  return list.filter(item => !isNsfwContent(item));
};

// Filtered Computed Properties
const filteredMovies = computed(() => filterNsfw(props.movies));
const filteredSeries = computed(() => filterNsfw(props.series));
const filteredAnimeMovies = computed(() => filterNsfw(props.animeMovies));
const filteredAnimeSeries = computed(() => filterNsfw(props.animeSeries));
const filteredKdramaMovies = computed(() => filterNsfw(props.kdramaMovies));
const filteredKdramaSeries = computed(() => filterNsfw(props.kdramaSeries));
const filteredBlMovies = computed(() => filterNsfw(props.blMovies));
const filteredBlSeries = computed(() => filterNsfw(props.blSeries));
const filteredRecommended = computed(() => filterNsfw(props.recommended));

// Hero items = top-rated from movies + series
const heroItems = computed(() => {
  const combined = [...filteredMovies.value.slice(0, 3), ...filteredSeries.value.slice(0, 3)];
  return combined.length > 0 ? combined.slice(0, 5) : [];
});

const topTenMovies = computed(() => {
  return filteredMovies.value.slice(0, 10);
});

const topTenSeries = computed(() => {
  return filteredSeries.value.slice(0, 10);
});

const handleFavorite = (item) => {
  console.log('Favorited:', item.title);
};
</script>
