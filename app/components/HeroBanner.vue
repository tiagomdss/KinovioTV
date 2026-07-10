<template>
  <section
    class="relative w-full min-h-[68svh] md:min-h-[72vh] overflow-hidden"
    aria-roledescription="carrossel"
    @mouseenter="pause"
    @mouseleave="resume"
    @focusin="pause"
    @focusout="resume"
    @touchstart.passive="onTouchStart"
    @touchend.passive="onTouchEnd"
  >
    <!-- Background Images with Ken Burns -->
    <div class="absolute inset-0 z-0">
      <div 
        v-for="(item, index) in displayItems"
        :key="item.id || index"
        class="absolute inset-0 transition-opacity duration-1000"
        :class="index === activeIndex ? 'opacity-100' : 'opacity-0'"
      >
        <img
          v-if="item.backdrop || item.poster"
          :src="item.backdrop || item.poster" 
          :alt="item.title"
          class="w-full h-full object-cover hero-image"
          :class="index === activeIndex ? 'hero-image-active' : ''"
        />
      </div>
      <!-- Gradients -->
      <div class="absolute inset-0 bg-gradient-to-t from-cinema-deep via-cinema-deep/45 to-black/10"></div>
      <div class="absolute inset-0 bg-gradient-to-r from-cinema-deep/85 via-cinema-deep/15 to-transparent"></div>
      <div class="absolute bottom-0 left-0 right-0 h-40 bg-gradient-to-t from-cinema-deep to-transparent"></div>
    </div>

    <!-- Content -->
    <div class="relative z-10 flex flex-col justify-end min-h-[68svh] md:min-h-[72vh] px-5 sm:px-8 lg:px-14 pb-10 sm:pb-14">
      <!-- Tag -->
      <div class="flex items-center gap-2 mb-3 animate-fade-in">
        <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider bg-accent/15 text-accent border border-accent/25">
          <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 24 24"><path d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 8.715L18 9.75l-.259-1.035a3.375 3.375 0 00-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 002.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 002.455 2.456L21.75 6l-1.036.259a3.375 3.375 0 00-2.455 2.456z"/></svg>
          Destaque KinovioTV
        </span>
      </div>

      <!-- Title -->
      <h1 class="text-[2.15rem] sm:text-5xl lg:text-6xl font-black tracking-tight text-white leading-[0.95] max-w-3xl animate-slide-up text-balance">
        {{ displayItems[activeIndex]?.title || 'Carregando...' }}
      </h1>

      <!-- Ratings -->
      <div class="flex flex-wrap items-center gap-2 mt-3" v-if="displayItems[activeIndex]?.ratings">
        <span 
          v-for="r in displayItems[activeIndex].ratings" 
          :key="r.label"
          class="badge"
          :class="r.color || 'badge-imdb'"
        >
          {{ r.label }} {{ r.value }}
        </span>
      </div>

      <!-- Description -->
      <p class="text-sm text-white/72 leading-relaxed max-w-xl mt-3 line-clamp-2 sm:line-clamp-3">
        {{ displayItems[activeIndex]?.description || '' }}
      </p>

      <!-- Meta -->
      <div class="flex items-center gap-2 mt-2 text-[10px] font-medium text-text-muted">
        <span v-if="displayItems[activeIndex]?.year">{{ displayItems[activeIndex].year }}</span>
        <span v-if="displayItems[activeIndex]?.genre" class="w-1 h-1 rounded-full bg-text-muted"></span>
        <span v-if="displayItems[activeIndex]?.genre">{{ displayItems[activeIndex].genre }}</span>
        <span v-if="displayItems[activeIndex]?.duration" class="w-1 h-1 rounded-full bg-text-muted"></span>
        <span v-if="displayItems[activeIndex]?.duration">{{ displayItems[activeIndex].duration }}</span>
      </div>

      <!-- Action Buttons -->
      <div class="flex items-center gap-3 mt-5">
        <button
          id="hero-play"
          class="focusable min-h-12 px-6 sm:px-8 bg-white hover:bg-white/90 text-black font-bold rounded-xl flex items-center gap-2.5 transition-smooth active:scale-[0.98] shadow-lg text-sm"
          @click="$emit('play', displayItems[activeIndex])"
        >
          <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg>
          Assistir
        </button>
        
        <button 
          id="hero-info"
          class="focusable min-h-12 px-5 glass-heavy hover:bg-glass-hover text-white font-semibold rounded-xl flex items-center gap-2 transition-smooth active:scale-[0.98] text-sm"
          @click="$emit('info', displayItems[activeIndex])"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
          Detalhes
        </button>

        <button 
          id="hero-fav"
          class="focusable w-12 h-12 glass-heavy hover:bg-glass-hover text-white rounded-xl transition-smooth active:scale-[0.98] flex items-center justify-center"
          @click="$emit('favorite', displayItems[activeIndex])"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5C7 .5 2 4.5 2 9c0 5 10 12 10 12s10-7 10-12c0-4.5-5-8.5-10-4.5z"/></svg>
        </button>
      </div>

      <!-- Dot Indicators -->
      <div class="flex items-center gap-1.5 mt-6" v-if="displayItems.length > 1">
        <button
          v-for="(_, index) in displayItems"
          :key="index"
          class="dot-indicator"
          :class="{ active: index === activeIndex }"
          @click="goTo(index)"
          :aria-label="`Slide ${index + 1}`"
        ></button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';

const props = defineProps({
  items: { type: Array, default: () => [] },
  interval: { type: Number, default: 8000 },
});

defineEmits(['play', 'info', 'favorite']);

const activeIndex = ref(0);
let timer = null;
let touchStartX = 0;

const displayItems = computed(() => {
  if (props.items.length === 0) {
    return [{
      title: 'KinovioTV',
      description: 'Seu cinema pessoal. Configure seus addons em Configurações para começar.',
      backdrop: '',
      ratings: [],
    }];
  }
  return props.items.slice(0, 6);
});

const goTo = (index) => {
  activeIndex.value = index;
  resetTimer();
};

const next = () => {
  activeIndex.value = (activeIndex.value + 1) % displayItems.value.length;
};

const previous = () => {
  activeIndex.value = (activeIndex.value - 1 + displayItems.value.length) % displayItems.value.length;
};

const pause = () => { if (timer) clearInterval(timer); timer = null; };
const resume = () => resetTimer();
const onTouchStart = (event) => { touchStartX = event.changedTouches?.[0]?.clientX || 0; pause(); };
const onTouchEnd = (event) => {
  const delta = (event.changedTouches?.[0]?.clientX || 0) - touchStartX;
  if (Math.abs(delta) > 44) delta < 0 ? next() : previous();
  resume();
};

const resetTimer = () => {
  pause();
  if (displayItems.value.length > 1) {
    timer = setInterval(next, props.interval);
  }
};

onMounted(() => {
  resetTimer();
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>
