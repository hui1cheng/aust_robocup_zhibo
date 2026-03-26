<template>
  <div class="cyber-container">
    <div class="grid-overlay"></div>
    <div class="scanline"></div>
    <router-view v-slot="{ Component }">
      <transition name="fade-slide" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style scoped>
.cyber-container {
  position: fixed; inset: 0; background: #020406; overflow: hidden;
  font-family: 'Courier New', monospace; color: #00f3ff;
}
.grid-overlay {
  position: absolute; inset: 0; pointer-events: none; opacity: 0.05;
  background-image: linear-gradient(#00f3ff 1px, transparent 1px), linear-gradient(90deg, #00f3ff 1px, transparent 1px);
  background-size: 40px 40px;
}
.scanline {
  position: absolute; top: 0; width: 100%; height: 2px; opacity: 0.3;
  background: #00f3ff; box-shadow: 0 0 10px #00f3ff; animation: scan 8s linear infinite;
}
@keyframes scan { from { top: -10% } to { top: 110% } }

.fade-slide-enter-active, .fade-slide-leave-active { transition: 0.4s ease; }
.fade-slide-enter-from { opacity: 0; transform: translateY(20px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-20px); }
</style>