<template>
  <MagneticPointer />
  <div id="app">
      <router-view></router-view>
  </div>
</template>
<script setup>
import LivePlayer from './components/LivePlayer.vue';
import MagneticPointer from './components/MagneticPointer.vue'
</script>

<style>
/* App.vue 的 style */
#app {
  cursor: none !important; /* 强制隐藏原生箭头 */
}
/* 全局隐藏原始鼠标指针 */
* {
  cursor: none !important;
}


/* 确保所有能交互的元素也不显示手型 */
a, button, input, .magnetic-target {
  cursor: none !important;
}</style>