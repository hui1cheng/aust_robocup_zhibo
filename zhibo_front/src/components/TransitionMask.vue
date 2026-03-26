<template>
  <div v-show="isVisible" class="mask-container">
    <svg ref="svgRef" class="loading-svg" viewBox="0 0 1000 1000">
      <defs>
        <polygon id="h" points="0,-50 43.3,-25 43.3,25 0,50 -43.3,25 -43.3,-25" fill="#050505" />
      </defs>
      <g v-for="l in 15" :key="'l'+l">
        <use 
          v-for="r in 15" :key="'r'+r"
          class="hexagon-unit"
          href="#h"
          :x="l % 2 ? 86.5 * (r-1) : 86.5 * (r-1) + 43.3"
          :y="74.5 * (l-1)"
        />
      </g>
    </svg>

    <div v-show="showText" class="fault-container">
      <p v-for="n in 4" :key="n" class="faulttext" :class="{ 'faulttext_fault': isGlitching }">
        安徽理工大学
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import gsap from 'gsap';

const isVisible = ref(false);
const showText = ref(false);
const isGlitching = ref(false);
const svgRef = ref(null);

let glitchTimer = null;

const play = (onComplete) => {
  isVisible.value = true;
  
  nextTick(() => {
    const blocks = document.querySelectorAll('.hexagon-unit');
    const textElements = document.querySelectorAll('.faulttext');
    
    const tl = gsap.timeline({
      onComplete: () => {
        stopGlitch();
        isVisible.value = false;
        if (onComplete) onComplete();
      }
    });

    // 1. 六边形背景入场
    tl.set(blocks, { scale: 0, opacity: 0, strokeDashoffset: 100 })
      .to(blocks, { 
        scale: 1, 
        opacity: 1, 
        strokeDashoffset: 0, 
        duration: 0.4, 
        stagger: { from: "random", amount: 0.3 } 
      })
      
    // 2. 文字出现并开始故障特效
      .add(() => {
        showText.value = true;
        startGlitch(textElements);
      })
      .fromTo(".fault-container", { scale: 1.5, opacity: 0 }, { scale: 1, opacity: 1, duration: 0.5, ease: "expo.out" })
      
    // 3. 持续故障时间
      .to({}, { duration: 1 }) 
      
    // 4. 整体收缩消失
      .add(() => { isGlitching.value = false; })
      .to(".fault-container", { scale: 0, opacity: 0, duration: 0.4, ease: "power4.in" }, "exit")
      .to(blocks, { 
        scale: 0, 
        opacity: 0, 
        duration: 0.6, 
        stagger: { from: "center", each: 0.003 } 
      }, "exit");
  });
};

// 故障逻辑实现
const startGlitch = (elements) => {
  isGlitching.value = true;
  glitchTimer = setInterval(() => {
    elements.forEach(text => {
      text.style.transform = `translate(${Math.random() * 40 - 20}%, ${Math.random() * 40 - 20}%)`;
      let x = Math.random() * 100, y = Math.random() * 100;
      let w = Math.random() * 50 + 10, h = Math.random() * 30 + 10;
      text.style.clipPath = `polygon(${x}% ${y}%, ${x+w}% ${y}%, ${x+w}% ${y+h}%, ${x}% ${y+h}%)`;
    });
  }, 40);
};

const stopGlitch = () => {
  clearInterval(glitchTimer);
  showText.value = false;
};

defineExpose({ play });
</script>

<style scoped>
/* TransitionMask.vue */
.mask-container {
  position: fixed; /* 必须是 fixed */
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: #000; /* 纯黑背景，确保能遮住后面的卡片 */
  z-index: 999999 !important; /* 给一个极大的数值，确保超过登录卡片的 9999 */
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  pointer-events: all; /* 拦截所有点击，防止动画时还能点到后面的按钮 */
}

/* 六边形样式 */
.loading-svg { width: 110%; height: auto; position: absolute; }
.hexagon-unit {
  stroke: #00f3ff;
  stroke-width: 0.8;
  stroke-dasharray: 100;
  stroke-opacity: 1;
  transform-origin: center;
}

/* 故障文字样式 */
.fault-container {
  position: relative;
  z-index: 10;
  display: flex; justify-content: center; align-items: center;
}

.faulttext {
  position: absolute;
  font-family: 'Impact', 'Microsoft YaHei', sans-serif;
  font-size: 6rem;
  color: #fff;
  white-space: nowrap;
  letter-spacing: 0.8rem;
  user-select: none;
}

/* 故障时的红蓝重影 */
.faulttext_fault::after,
.faulttext_fault::before {
  content: "安徽理工大学";
  position: absolute;
  left: 0; top: 0;
  mix-blend-mode: screen;
  width: 100%;
}

.faulttext_fault::after {
  color: #ff0000;
  transform: translateX(3%);
}

.faulttext_fault::before {
  color: #00f3ff; /* 改为青色更配合校名 */
  transform: translateX(-3%);
}
</style>