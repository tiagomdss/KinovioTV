<template>
  <div class="relative group/carousel w-full">
    <div class="flex items-center justify-between mb-4 px-1">
      <h2 class="text-base sm:text-lg font-bold text-text-primary tracking-tight flex items-center gap-2">
        <span class="text-accent font-black text-xl">🏆</span>
        {{ title }}
      </h2>
    </div>

    <!-- Left Scroll Button -->
    <button 
      v-if="hasMouse && items.length > 0"
      class="absolute left-2 top-[55%] -translate-y-1/2 z-30 w-10 h-10 rounded-full bg-black/60 hover:bg-accent border border-glass-border/40 text-white flex items-center justify-center transition-smooth opacity-0 group-hover/carousel:opacity-100 shadow-lg shadow-black/30 hover:scale-110"
      @click="scrollRow('left')"
      title="Anterior"
    >
      <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
    </button>

    <div 
      ref="track"
      class="carousel-snap"
      style="gap: 0.5rem;"
    >
      <div
        v-for="(item, index) in items.slice(0, 10)"
        :key="item.id || index"
        :id="`top10-${index}`"
        class="carousel-item focusable flex-shrink-0 flex items-end cursor-pointer group relative"
        style="width: 200px; height: 240px;"
        @click="$emit('select', item)"
      >
        <!-- Big Number -->
        <span class="top-number absolute -left-1 bottom-0 z-10 select-none font-black" style="font-size: 7rem; line-height: 1;">
          {{ index + 1 }}
        </span>

        <!-- Poster -->
        <div class="absolute right-0 top-0 w-[130px] h-full rounded-xl overflow-hidden border border-glass-border shadow-card transition-smooth group-hover:scale-[1.03] group-hover:border-accent/30">
          <img 
            v-if="item.poster || item.backdrop"
            :src="item.poster || item.backdrop" 
            :alt="item.title"
            loading="lazy"
            class="w-full h-full object-cover animate-fade-in"
          />
          <div v-else class="skeleton w-full h-full"></div>
          
          <!-- Bottom gradient -->
          <div class="absolute bottom-0 left-0 right-0 h-16 bg-gradient-to-t from-black/80 to-transparent"></div>
          
          <!-- Rating badge -->
          <span v-if="item.rating" class="badge badge-imdb absolute bottom-2 left-2">{{ item.rating }}</span>
        </div>
      </div>
    </div>

    <!-- Right Scroll Button -->
    <button 
      v-if="hasMouse && items.length > 0"
      class="absolute right-2 top-[55%] -translate-y-1/2 z-30 w-10 h-10 rounded-full bg-black/60 hover:bg-accent border border-glass-border/40 text-white flex items-center justify-center transition-smooth opacity-0 group-hover/carousel:opacity-100 shadow-lg shadow-black/30 hover:scale-110"
      @click="scrollRow('right')"
      title="Próximo"
    >
      <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"/></svg>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

defineProps({
  items: { type: Array, default: () => [] },
  title: { type: String, default: 'Top 10 da Semana' },
});

defineEmits(['select']);

const track = ref(null);
const hasMouse = ref(false);

onMounted(() => {
  hasMouse.value = window.matchMedia('(pointer: fine)').matches;
  const detectMouse = () => {
    hasMouse.value = true;
    window.removeEventListener('mousemove', detectMouse);
  };
  window.addEventListener('mousemove', detectMouse);
});

const scrollRow = (direction) => {
  const container = track.value;
  if (!container) return;
  const scrollAmount = container.clientWidth * 0.75;
  container.scrollBy({
    left: direction === 'left' ? -scrollAmount : scrollAmount,
    behavior: 'smooth'
  });
};
</script>
