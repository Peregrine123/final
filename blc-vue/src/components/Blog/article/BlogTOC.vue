<template>
  <div class="blog-toc" v-if="headers.length">
    <div class="toc-title">目录</div>
    <ul class="toc-list">
      <li 
        v-for="header in headers" 
        :key="header.id"
        :class="['toc-item', `level-${header.level}`, { active: activeId === header.id }]"
        @click.prevent="scrollTo(header.id)"
      >
        {{ header.text }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'BlogTOC',
  props: {
    headers: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      activeId: ''
    };
  },
  mounted() {
    window.addEventListener('scroll', this.onScroll);
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.onScroll);
  },
  methods: {
    scrollTo(id) {
      const el = document.getElementById(id);
      if (el) {
        const offset = 80; // Header height offset
        const top = el.getBoundingClientRect().top + window.pageYOffset - offset;
        window.scrollTo({ top, behavior: 'smooth' });
        // Set active immediately
        this.activeId = id;
      }
    },
    onScroll() {
      // Simple scroll spy
      // Find the last header that is above a certain threshold
      const threshold = 150;
      let currentId = '';
      
      for (const header of this.headers) {
        const el = document.getElementById(header.id);
        if (el) {
          const rect = el.getBoundingClientRect();
          // If header is above threshold (scrolled past or near top)
          if (rect.top < threshold) {
            currentId = header.id;
          } else {
            // Once we hit a header below threshold, the previous one was the current one
            break;
          }
        }
      }
      
      if (currentId) {
          this.activeId = currentId;
      } else if (this.headers.length > 0) {
          // If at very top, highlight first
          const first = document.getElementById(this.headers[0].id);
          if(first && first.getBoundingClientRect().top > threshold) {
              this.activeId = ''; // Or this.headers[0].id
          }
      }
    }
  }
}
</script>

<style scoped>
.blog-toc {
  position: sticky;
  top: 100px; /* Space for fixed header if any */
  max-height: calc(100vh - 120px);
  overflow-y: auto;
  padding-left: 16px;
  border-left: 2px solid #e2e8f0;
}

.toc-title {
  font-weight: 700;
  margin-bottom: 12px;
  font-size: 0.9rem;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.toc-item {
  margin-bottom: 8px;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  transition: color 0.2s;
  line-height: 1.4;
  display: block;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.toc-item:hover {
  color: #3b82f6;
}

.toc-item.active {
  color: #3b82f6;
  font-weight: 600;
  border-left: 2px solid #3b82f6; /* Optional visual cue */
  margin-left: -18px; /* Compensate for border-left of container? No, just visual shift */
  padding-left: 16px; /* Reset padding to align */
  background: linear-gradient(90deg, rgba(59,130,246,0.05) 0%, rgba(255,255,255,0) 100%);
}

.level-1 { padding-left: 0; }
.level-2 { padding-left: 12px; }
.level-3 { padding-left: 24px; }
</style>
