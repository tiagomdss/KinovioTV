<template>
  <!-- Poster Skeleton (vertical) -->
  <div v-if="variant === 'poster'" class="skeleton rounded-2xl" :style="{ width: width, aspectRatio: '2/3' }"></div>

  <!-- Landscape Skeleton (continue watching) -->
  <div v-else-if="variant === 'landscape'" class="skeleton rounded-2xl" :style="{ width: width, aspectRatio: '16/9' }"></div>

  <!-- Hero Skeleton -->
  <div v-else-if="variant === 'hero'" class="w-full flex flex-col gap-4 p-6">
    <div class="skeleton h-8 w-48 rounded-lg"></div>
    <div class="skeleton h-12 w-80 rounded-lg"></div>
    <div class="skeleton h-4 w-64 rounded-lg"></div>
    <div class="flex gap-3 mt-2">
      <div class="skeleton h-12 w-36 rounded-2xl"></div>
      <div class="skeleton h-12 w-12 rounded-2xl"></div>
    </div>
  </div>

  <!-- Text line skeleton -->
  <div v-else-if="variant === 'text'" class="skeleton rounded-md" :style="{ width: width, height: height || '14px' }"></div>

  <!-- Chip skeleton -->
  <div v-else-if="variant === 'chip'" class="skeleton rounded-full" :style="{ width: width || '80px', height: '32px' }"></div>

  <!-- Default block skeleton -->
  <div v-else class="skeleton rounded-2xl" :style="{ width: width, height: height }"></div>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'poster', // poster | landscape | hero | text | chip | block
  },
  width: {
    type: String,
    default: '160px',
  },
  height: {
    type: String,
    default: '240px',
  },
});
</script>
