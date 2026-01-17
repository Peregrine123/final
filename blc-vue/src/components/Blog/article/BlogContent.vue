<template>
  <div class="blog-content">
    <div class="markdown-body" ref="content" v-html="content"></div>
  </div>
</template>

<script>
export default {
  name: 'BlogContent',
  props: {
    content: {
      type: String,
      default: ''
    }
  },
  watch: {
    content: {
      handler() {
        this.$nextTick(() => {
          this.enhanceContent();
        });
      },
      immediate: true
    }
  },
  methods: {
    enhanceContent() {
      const root = this.$refs.content;
      if (!root) return;

      // 1. Add copy buttons to code blocks
      const preBlocks = root.querySelectorAll('pre');
      preBlocks.forEach((pre) => {
        // Check if already enhanced
        if (pre.querySelector('.copy-btn')) return;

        const btn = document.createElement('button');
        btn.className = 'copy-btn';
        btn.textContent = 'Copy';
        btn.onclick = () => {
          const code = pre.querySelector('code')?.innerText || pre.innerText;
          if (navigator.clipboard) {
              navigator.clipboard.writeText(code).then(() => {
                btn.textContent = 'Copied!';
                setTimeout(() => btn.textContent = 'Copy', 2000);
              }).catch(err => {
                  console.error('Failed to copy', err);
                  btn.textContent = 'Error';
              });
          } else {
             btn.textContent = 'N/A';
          }
        };
        
        pre.style.position = 'relative';
        pre.appendChild(btn);
        
        // Add mac-style dots if not present
        if (!pre.querySelector('.mac-dots')) {
            const dots = document.createElement('div');
            dots.className = 'mac-dots';
            dots.innerHTML = '<span></span><span></span><span></span>';
            pre.insertBefore(dots, pre.firstChild);
        }
      });

      // 2. Wrap tables for responsiveness
      const tables = root.querySelectorAll('table');
      tables.forEach(table => {
        if (!table.parentElement.classList.contains('table-wrapper')) {
          const wrapper = document.createElement('div');
          wrapper.className = 'table-wrapper';
          table.parentNode.insertBefore(wrapper, table);
          wrapper.appendChild(table);
        }
      });

      // 3. Parse headers for TOC and inject IDs
      const headerTags = Array.from(root.querySelectorAll('h1, h2, h3'));
      const headers = headerTags.map((h, index) => {
        const id = `heading-${index}`;
        if (!h.id) h.id = id;
        return {
          id: h.id,
          text: h.innerText,
          level: parseInt(h.tagName.substring(1))
        };
      });
      
      this.$emit('headers-parsed', headers);
    }
  }
}
</script>

<style>
/* Not scoped to affect v-html */
.blog-content .markdown-body {
  font-family: 'Merriweather', 'Georgia', serif; /* Serif for body */
  font-size: 18px;
  line-height: 1.8;
  color: #2c3e50;
}

.blog-content .markdown-body h1,
.blog-content .markdown-body h2,
.blog-content .markdown-body h3,
.blog-content .markdown-body h4 {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: #1a202c;
  margin-top: 2em;
  margin-bottom: 0.8em;
  font-weight: 700;
  scroll-margin-top: 100px; /* Offset for sticky header/TOC scroll */
}

.blog-content .markdown-body p {
  margin-bottom: 1.5em;
}

.blog-content .markdown-body a {
  color: #3b82f6;
  text-decoration: none;
  border-bottom: 2px solid rgba(59, 130, 246, 0.2);
  transition: all 0.2s;
}

.blog-content .markdown-body a:hover {
  background: rgba(59, 130, 246, 0.1);
  border-bottom-color: #3b82f6;
}

.blog-content .markdown-body blockquote {
  border-left: 4px solid #3b82f6;
  background: #f8fafc;
  padding: 1rem 1.5rem;
  margin: 1.5rem 0;
  font-style: italic;
  color: #475569;
  border-radius: 0 8px 8px 0;
}

/* Code Blocks */
.blog-content .markdown-body pre {
  background: #1e293b;
  border-radius: 8px;
  padding: 3rem 1.5rem 1.5rem; /* Top padding for dots */
  overflow-x: auto;
  color: #e2e8f0;
  font-family: 'Fira Code', monospace;
  position: relative;
  margin: 1.5rem 0;
}

.blog-content .mac-dots {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 6px;
}

.blog-content .mac-dots span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #cbd5e1;
}
.blog-content .mac-dots span:nth-child(1) { background: #ff5f56; }
.blog-content .mac-dots span:nth-child(2) { background: #ffbd2e; }
.blog-content .mac-dots span:nth-child(3) { background: #27c93f; }

.blog-content .copy-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(255,255,255,0.1);
  border: none;
  color: #94a3b8;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}
.blog-content .copy-btn:hover {
  background: rgba(255,255,255,0.2);
  color: #fff;
}

/* Images */
.blog-content .markdown-body img {
  max-width: 100%;
  border-radius: 8px;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
  margin: 2rem auto;
  display: block;
}

/* Tables */
.blog-content .table-wrapper {
  overflow-x: auto;
  margin: 1.5rem 0;
}
.blog-content .markdown-body table {
  width: 100%;
  border-collapse: collapse;
}
.blog-content .markdown-body th,
.blog-content .markdown-body td {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
}
.blog-content .markdown-body th {
  background: #f1f5f9;
  font-weight: 600;
}
</style>
