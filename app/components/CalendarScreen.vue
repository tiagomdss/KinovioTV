<template>
  <section class="calendar-screen min-h-screen px-5 pt-8 pb-32 sm:px-8 lg:px-14 lg:pt-12">
    <header class="max-w-6xl mx-auto">
      <p class="text-[11px] font-bold uppercase tracking-[0.18em] text-accent">Sua semana</p>
      <div class="mt-2 flex items-end justify-between gap-4">
        <div>
          <h1 class="text-3xl sm:text-5xl font-black tracking-tight text-white">Calendário</h1>
          <p class="mt-2 text-sm text-text-secondary">Episódios e estreias dos títulos que você acompanha.</p>
        </div>
        <button class="focusable hidden sm:inline-flex min-h-11 items-center gap-2 rounded-xl border border-white/10 bg-white/5 px-4 text-sm font-semibold text-white hover:bg-white/10">
          <CalendarDays :size="17" /> Hoje
        </button>
      </div>

      <div class="mt-7 flex gap-2 overflow-x-auto no-scrollbar pb-2" aria-label="Dias da semana">
        <button
          v-for="(day, index) in days"
          :key="day.label"
          class="focusable min-w-[64px] min-h-[64px] rounded-xl border px-3 py-2 text-center transition-smooth"
          :class="index === activeDay ? 'border-accent/60 bg-accent/15 text-white shadow-glow-accent' : 'border-white/8 bg-white/[0.03] text-text-secondary hover:bg-white/[0.07]'"
          @click="activeDay = index"
        >
          <span class="block text-[10px] font-semibold uppercase">{{ day.weekday }}</span>
          <span class="mt-1 block text-lg font-bold tabular-nums">{{ day.number }}</span>
        </button>
      </div>
    </header>

    <div class="max-w-6xl mx-auto mt-8">
      <div v-if="visibleItems.length" class="grid gap-3">
        <button
          v-for="(item, index) in visibleItems"
          :key="item.id || index"
          class="focusable group grid min-h-[104px] grid-cols-[112px_1fr_auto] items-center gap-4 overflow-hidden rounded-2xl border border-white/8 bg-white/[0.035] p-2 text-left transition-smooth hover:border-accent/35 hover:bg-white/[0.065]"
          @click="$emit('select', item)"
        >
          <img :src="item.backdrop || item.poster" :alt="item.title" class="h-[88px] w-28 rounded-xl object-cover bg-cinema-elevated" loading="lazy" />
          <span class="min-w-0">
            <span class="block text-[10px] font-bold uppercase tracking-wider text-accent">Novo episódio</span>
            <strong class="mt-1 block truncate text-sm sm:text-base text-white">{{ item.title }}</strong>
            <span class="mt-1 block truncate text-xs text-text-secondary">{{ episodeLabel(index) }} · Disponível hoje</span>
          </span>
          <ChevronRight class="mr-2 text-text-muted transition-transform group-hover:translate-x-1 group-hover:text-white" :size="20" />
        </button>
      </div>

      <div v-else class="mt-8 rounded-2xl border border-white/8 bg-white/[0.03] px-6 py-14 text-center">
        <CalendarX class="mx-auto text-text-muted" :size="36" />
        <h2 class="mt-4 text-base font-bold text-white">Nada programado para este dia</h2>
        <p class="mt-2 text-sm text-text-secondary">Adicione séries à sua lista para acompanhar novos episódios.</p>
        <button class="focusable mt-5 min-h-11 rounded-xl bg-accent px-5 text-sm font-bold text-white hover:bg-accent-hover" @click="$emit('explore')">Explorar títulos</button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, ref } from 'vue';
import { CalendarDays, CalendarX, ChevronRight } from 'lucide-vue-next';

const props = defineProps({ items: { type: Array, default: () => [] } });
defineEmits(['select', 'explore']);

const activeDay = ref(0);
const formatter = new Intl.DateTimeFormat('pt-BR', { weekday: 'short' });
const days = Array.from({ length: 7 }, (_, index) => {
  const date = new Date();
  date.setDate(date.getDate() + index);
  return { weekday: index === 0 ? 'Hoje' : formatter.format(date).replace('.', ''), number: date.getDate(), label: date.toISOString() };
});

const visibleItems = computed(() => activeDay.value > 3 ? [] : props.items.slice(activeDay.value * 2, activeDay.value * 2 + 5));
const episodeLabel = (index) => `T${String((index % 2) + 1).padStart(2, '0')} · E${String(index + 1).padStart(2, '0')}`;
</script>

<style scoped>
.calendar-screen { background: radial-gradient(circle at 50% -15%, rgba(122,77,255,.16), transparent 35%); }
</style>
