

<template>
  <div class="picker-wrapper">
    <div class="grid">
       <div 
         v-for="r in sectors" 
         :key="r.key" 
         @click="handleSelect(r.key)" 
         class="card magnetic-target"
       >
         <div class="card-frame-gif"></div>
         <div class="icon">{{ r.icon }}</div>
         <div class="name">{{ r.name }}</div>
       </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
// 如果还没引入背景，记得带上
// import AsciiBackground from './AsciiBackground.vue';

const router = useRouter();

const sectors = [
  { key: '2d', name: '2D SOCCER', icon: '⚽' },
  { key: '3d', name: '3D SOCCER', icon: '🎮' },
  { key: 'rescue', name: 'RESCUE', icon: '🚨' }
];

const handleSelect = (key) => {
  console.log("Target Key:", key);
  router.push({ name: 'Monitor', params: { type: key } })
    .then(() => console.log("Jump Success"))
    .catch(err => console.error("Jump Fail:", err));
};
</script>

<style scoped>
.picker-wrapper { height: 100%; display: flex; flex-direction: column; justify-content: center; align-items: center; }
.grid { display: flex; gap: 30px; }
.card {
  width: 220px; padding: 40px; background: rgba(10, 20, 30, 0.8);
  border: 1px solid rgba(0, 243, 255, 0.2); cursor: pointer; text-align: center;
  transition: 0.3s;
}
.card:hover { border-color: #00f3ff; box-shadow: 0 0 20px #00f3ff; transform: translateY(-10px); }
.icon { font-size: 50px; margin-bottom: 20px; }
</style>