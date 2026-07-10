<template>
  <div class="flex gap-2 overflow-x-auto no-scrollbar px-1 pb-1">
    <button
      v-for="cat in categories"
      :key="cat.id"
      :id="`cat-${cat.id}`"
      class="focusable inline-flex min-h-11 shrink-0 items-center gap-2 rounded-xl border px-4 text-xs font-semibold transition-smooth"
      :class="activeCategory === cat.id
        ? 'bg-accent/15 border-accent/35 text-white shadow-sm shadow-accent/10'
        : 'border-white/8 bg-white/[0.035] text-text-secondary hover:border-white/15 hover:text-white'"
      @click="selectCategory(cat)"
    >
      <component :is="cat.icon" :size="15" :stroke-width="1.8" />
      <span>{{ cat.label }}</span>
    </button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { LayoutGrid, Zap, Laugh, Drama, Ghost, Heart, Rocket, Search, Sparkles, Globe2, Film, WandSparkles, Music2 } from 'lucide-vue-next';

const props = defineProps({ modelValue: { type: String, default: 'all' } });
const emit = defineEmits(['update:modelValue', 'select']);
const activeCategory = ref(props.modelValue || 'all');

watch(() => props.modelValue, value => { activeCategory.value = value || 'all'; });

const categories = [
  { id: 'all', label: 'Todos', icon: LayoutGrid },
  { id: 'action', label: 'Ação', icon: Zap },
  { id: 'comedy', label: 'Comédia', icon: Laugh },
  { id: 'drama', label: 'Drama', icon: Drama },
  { id: 'horror', label: 'Terror', icon: Ghost },
  { id: 'romance', label: 'Romance', icon: Heart },
  { id: 'scifi', label: 'Ficção científica', icon: Rocket },
  { id: 'thriller', label: 'Suspense', icon: Search },
  { id: 'anime', label: 'Anime', icon: Sparkles },
  { id: 'kdrama', label: 'K-Drama', icon: Globe2 },
  { id: 'documentary', label: 'Documentário', icon: Film },
  { id: 'fantasy', label: 'Fantasia', icon: WandSparkles },
  { id: 'musical', label: 'Musical', icon: Music2 },
];

const selectCategory = (cat) => {
  activeCategory.value = cat.id;
  emit('update:modelValue', cat.id);
  emit('select', cat);
};
</script>
