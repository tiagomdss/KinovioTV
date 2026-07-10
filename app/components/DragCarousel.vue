<template>
  <div class="relative group/carousel">
    <!-- Section Header -->
    <div v-if="title" class="flex items-center justify-between mb-4 px-1">
      <div class="flex items-center gap-2.5">
        <h2 class="text-base sm:text-lg font-bold text-text-primary tracking-tight">{{ title }}</h2>
        <span v-if="subtitle" class="text-[10px] font-semibold text-text-muted bg-glass rounded-md px-2 py-0.5 border border-glass-border">{{ subtitle }}</span>
      </div>
      <button 
        v-if="items.length > 3"
        class="text-[11px] font-semibold text-text-secondary hover:text-accent transition-fast flex items-center gap-1"
        @click="$emit('see-all')"
      >
        Ver tudo
        <svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"/></svg>
      </button>
    </div>

    <!-- Scroll Container -->
    <div class="relative">
      <!-- Arrow Left -->
      <button 
        v-if="canScrollLeft"
        class="hidden md:flex absolute left-0 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full glass-heavy items-center justify-center text-white opacity-0 group-hover/carousel:opacity-100 transition-smooth hover:bg-glass-hover -ml-2"
        @click="scrollBy(-1)"
        aria-label="Anterior"
      >
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7"/></svg>
      </button>

      <!-- Carousel Track -->
      <div 
        ref="track"
        class="carousel-snap"
        :class="{ 'is-dragging': isDragging }"
        :style="{ gap: gap }"
        @mousedown="startDrag"
        @touchstart.passive="startTouch"
        @scroll.passive="updateScrollState"
      >
        <!-- Loading Skeletons -->
        <template v-if="loading">
          <SkeletonLoader 
            v-for="i in skeletonCount"
            :key="`skeleton-${i}`"
            :variant="layout === 'poster' ? 'poster' : 'landscape'"
            :width="layout === 'poster' ? '150px' : '300px'"
            class="carousel-item"
          />
        </template>

        <!-- Real Items -->
        <template v-else>
          <slot :items="items">
            <MediaCard
              v-for="(item, index) in items"
              :key="item.id || index"
              :item="item"
              :layout="layout"
              :badge="badge"
              :badge-type="badgeType"
              :card-id="`${carouselId}-${index}`"
              @select="$emit('select', item)"
              @hover="$emit('hover', item)"
            />
          </slot>
        </template>
      </div>

      <!-- Arrow Right -->
      <button 
        v-if="canScrollRight"
        class="hidden md:flex absolute right-0 top-1/2 -translate-y-1/2 z-20 w-10 h-10 rounded-full glass-heavy items-center justify-center text-white opacity-0 group-hover/carousel:opacity-100 transition-smooth hover:bg-glass-hover -mr-2"
        @click="scrollBy(1)"
        aria-label="Próximo"
      >
        <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5"><path stroke-linecap="round" stroke-linejoin="round" d="M9 5l7 7-7 7"/></svg>
      </button>

      <!-- Edge Gradients -->
      <div v-if="canScrollLeft" class="absolute left-0 top-0 bottom-0 w-12 bg-gradient-to-r from-cinema-deep to-transparent pointer-events-none z-10"></div>
      <div v-if="canScrollRight" class="absolute right-0 top-0 bottom-0 w-12 bg-gradient-to-l from-cinema-deep to-transparent pointer-events-none z-10"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue';

const props = defineProps({
  title: { type: String, default: '' },
  subtitle: { type: String, default: '' },
  items: { type: Array, default: () => [] },
  layout: { type: String, default: 'poster' },
  badge: { type: String, default: '' },
  badgeType: { type: String, default: '' },
  loading: { type: Boolean, default: false },
  skeletonCount: { type: Number, default: 6 },
  gap: { type: String, default: '0.75rem' },
  carouselId: { type: String, default: 'carousel' },
});

defineEmits(['select', 'hover', 'see-all']);

const track = ref(null);
const isDragging = ref(false);
const canScrollLeft = ref(false);
const canScrollRight = ref(true);

let startX = 0;
let scrollLeft = 0;
let rafId = null;

// Mouse drag
const startDrag = (e) => {
  if (!track.value) return;
  const startPageX = e.pageX;
  const startScrollLeft = track.value.scrollLeft;
  
  const onMove = (ev) => {
    const deltaX = Math.abs(ev.pageX - startPageX);
    // Only activate dragging state if user moved mouse more than 6px
    if (deltaX > 6) {
      isDragging.value = true;
    }
    
    if (isDragging.value) {
      ev.preventDefault();
      const walk = (ev.pageX - startPageX) * 1.3;
      if (rafId) cancelAnimationFrame(rafId);
      rafId = requestAnimationFrame(() => {
        if (track.value) track.value.scrollLeft = startScrollLeft - walk;
      });
    }
  };
  
  const onUp = () => {
    // Delay setting isDragging to false slightly to allow click event to capture
    setTimeout(() => {
      isDragging.value = false;
    }, 40);
    document.removeEventListener('mousemove', onMove);
    document.removeEventListener('mouseup', onUp);
  };
  
  document.addEventListener('mousemove', onMove);
  document.addEventListener('mouseup', onUp);
};

// Touch drag (passive, no prevent)
const startTouch = (e) => {
  // Let native scroll handle it — just track for state
  startX = e.touches[0].pageX;
  scrollLeft = track.value?.scrollLeft || 0;
};

// Scroll state
const updateScrollState = () => {
  if (!track.value) return;
  const el = track.value;
  canScrollLeft.value = el.scrollLeft > 10;
  canScrollRight.value = el.scrollLeft < el.scrollWidth - el.clientWidth - 10;
};

// Arrow navigation
const scrollBy = (direction) => {
  if (!track.value) return;
  const itemWidth = props.layout === 'poster' ? 170 : 340;
  const scrollAmount = itemWidth * 3 * direction;
  track.value.scrollBy({ left: scrollAmount, behavior: 'smooth' });
};

// Init
onMounted(() => {
  nextTick(() => updateScrollState());
  if (track.value) {
    const observer = new ResizeObserver(() => updateScrollState());
    observer.observe(track.value);
  }
});

watch(() => props.items, () => {
  nextTick(() => updateScrollState());
});
</script>
