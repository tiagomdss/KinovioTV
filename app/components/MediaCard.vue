<template>
  <div 
    class="carousel-item cursor-pointer group relative overflow-hidden transition-smooth"
    :class="[
      layout === 'poster' 
        ? 'w-[138px] sm:w-[154px] lg:w-[170px] rounded-xl' 
        : 'w-[278px] sm:w-[310px] lg:w-[340px] rounded-xl',
      'focusable'
    ]"
    :id="cardId"
    @click="$emit('select', item)"
    @mouseenter="$emit('hover', item)"
  >
    <!-- Poster Image -->
    <div 
      class="relative w-full overflow-hidden"
      :class="layout === 'poster' ? 'aspect-[2/3]' : 'aspect-video'"
    >
      <img 
        v-if="imageSrc && !imageErr" 
        :src="imageSrc"
        :alt="item.title"
        loading="lazy"
        class="w-full h-full object-cover transition-all duration-300 ease-smooth group-hover:scale-[1.035]"
        :class="shouldBlur ? 'blur-2xl scale-110 pointer-events-none select-none' : ''"
        @error="imageErr = true"
      />
      <!-- Premium Textual Placeholder Cover if image fails or is empty -->
      <div 
        v-else 
        class="w-full h-full bg-gradient-to-tr from-cinema-deep via-[#0d0d18] to-[#16162a] flex flex-col items-center justify-center p-4 text-center border border-glass-border/30 relative"
      >
        <span class="absolute inset-0 bg-gradient-to-br from-white/5 to-transparent pointer-events-none"></span>
        <svg class="w-6 h-6 text-text-muted/40 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M9.813 15.904L9 21m3.75-5.096l.813 5.096M3 9h18M3 15h18M5.25 3h13.5A2.25 2.25 0 0121 5.25v13.5A2.25 2.25 0 0118.75 21H5.25A2.25 2.25 0 013 18.75V5.25A2.25 2.25 0 015.25 3z" />
        </svg>
        <span class="text-[9px] font-extrabold text-text-secondary uppercase tracking-wider line-clamp-3 px-1">
          {{ item.title }}
        </span>
      </div>

      <!-- NSFW Blur Lock Overlay -->
      <div v-if="shouldBlur" class="absolute inset-0 z-20 bg-black/80 flex flex-col items-center justify-center p-2 text-center select-none">
        <svg class="w-5 h-5 mb-2 text-rose-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.8"><path stroke-linecap="round" stroke-linejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 00-9 0v3.75m-.75 0h10.5A2.25 2.25 0 0119.5 12.75v6A2.25 2.25 0 0117.25 21H6.75a2.25 2.25 0 01-2.25-2.25v-6a2.25 2.25 0 012.25-2.25z"/></svg>
        <span class="text-[8px] font-extrabold text-rose-500 uppercase tracking-widest leading-none">Bloqueado</span>
        <span class="text-[6px] text-text-muted mt-1 leading-tight max-w-[90px] block">Ative conteúdo +18 nas Configurações</span>
      </div>

      <!-- Gradient Overlay -->
      <div class="absolute inset-0 bg-gradient-to-t from-cinema-deep/90 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-smooth"></div>
      
      <!-- Progress Bar (continue watching) -->
      <div v-if="item.progress && item.progress > 0 && item.progress < 100" class="absolute bottom-0 left-0 w-full h-1 bg-white/10">
        <div class="h-full bg-accent progress-bar-glow rounded-full" :style="{ width: `${item.progress}%` }"></div>
      </div>

      <!-- Watched Badge -->
      <div v-if="item.progress >= 100 || item.watched" class="absolute top-2 right-2 w-5 h-5 rounded-full bg-emerald-500 flex items-center justify-center shadow-md">
        <svg class="w-3 h-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3"><path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7"/></svg>
      </div>

      <!-- Badges -->
      <div class="absolute bottom-2 left-2 flex flex-wrap gap-1" v-if="(!item.progress || item.progress <= 0) && !shouldBlur">
        <span v-if="item.rating" class="badge badge-imdb">{{ item.rating }}</span>
        <span v-if="badge" class="badge" :class="badgeClass">{{ badge }}</span>
        <span v-if="item.quality === '4K'" class="badge badge-4k">4K</span>
      </div>
      
      <!-- Play Button on Hover -->
      <div v-if="!shouldBlur" class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-smooth">
        <div class="w-12 h-12 rounded-full bg-white/20 backdrop-blur-sm flex items-center justify-center border border-white/30">
          <svg class="w-5 h-5 text-white ml-0.5" fill="currentColor" viewBox="0 0 24 24"><path d="M8 5v14l11-7z"/></svg>
        </div>
      </div>
    </div>

    <!-- Title Area -->
    <div v-if="showTitle" class="pt-2.5 px-0.5 pb-2">
      <h3 class="text-xs sm:text-[13px] font-semibold text-text-primary truncate leading-tight">
        {{ shouldBlur ? 'Conteúdo Ocultado (+18)' : item.title }}
      </h3>
      <p v-if="item.year || item.genre" class="text-[9px] text-text-muted mt-0.5 truncate">
        {{ item.year }}<span v-if="item.year && item.genre"> · </span>{{ shouldBlur ? 'Adulto' : item.genre }}
      </p>
      <!-- Episode Info (for continue watching) -->
      <p v-if="item.episodeLabel" class="text-[9px] text-text-secondary mt-0.5 truncate">
        {{ item.episodeLabel }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue';
import { isNsfwContent } from '~/utils/nsfw';

const props = defineProps({
  item: { type: Object, required: true },
  layout: { type: String, default: 'poster' }, // poster | landscape
  badge: { type: String, default: '' },
  badgeType: { type: String, default: '' }, // anime | kdrama | serie | new
  showTitle: { type: Boolean, default: true },
  cardId: { type: String, default: '' },
});

defineEmits(['select', 'hover']);

const imageErr = ref(false);
const enableNsfw = ref(false);

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

watch(() => props.item, () => {
  imageErr.value = false;
});

const isNsfw = computed(() => {
  return isNsfwContent(props.item);
});

const shouldBlur = computed(() => {
  return isNsfw.value && !enableNsfw.value;
});

const imageSrc = computed(() => {
  if (props.layout === 'poster') {
    return props.item.poster || props.item.backdrop || props.item.thumbnail;
  }
  return props.item.backdrop || props.item.thumbnail || props.item.poster;
});

const badgeClass = computed(() => {
  const map = {
    anime: 'badge-anime',
    kdrama: 'badge-kdrama',
    serie: 'badge-serie',
    new: 'badge-new',
    hd: 'badge-hd',
  };
  return map[props.badgeType] || 'badge-hd';
});
</script>
