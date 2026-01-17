<template>
  <transition
    @enter="onEnter"
    @leave="onLeave"
    :css="false"
  >
    <div v-if="isLoading" class="app-loading-overlay">
      <div class="loading-spinner"></div>
    </div>
  </transition>
</template>

<script>
import { mapState } from 'vuex';
import gsap from 'gsap';

export default {
  name: 'AppLoading',
  computed: {
    ...mapState(['isLoading'])
  },
  methods: {
    onEnter(el, done) {
      // Fade in background
      gsap.fromTo(el, 
        { opacity: 0 }, 
        { duration: 0.3, opacity: 1, ease: 'power2.out', onComplete: done }
      );
    },
    onLeave(el, done) {
      // Smooth fade out
      gsap.to(el, { 
        duration: 0.5, 
        opacity: 0, 
        ease: 'power2.in', 
        onComplete: done 
      });
    }
  }
};
</script>

<style scoped>
.app-loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #ffffff;
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 3px solid #ebeef5;
  border-top: 3px solid #409EFF; /* Element UI Blue */
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>