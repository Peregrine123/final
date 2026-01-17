<template>
  <div class="blog-hero" :style="heroStyle">
    <div class="hero-overlay"></div>
    <div class="hero-content">
      <el-button 
        class="back-btn" 
        type="text" 
        icon="el-icon-arrow-left" 
        @click="$emit('back')"
      >
        返回列表
      </el-button>
      
      <h1 class="article-title">{{ article.articleTitle }}</h1>
      
      <div class="article-meta">
        <div class="author-info">
          <el-avatar :size="32" :src="require('@/assets/logo.png')" class="author-avatar"></el-avatar>
          <span class="author-name">BLC Team</span>
        </div>
        
        <span class="divider">·</span>
        
        <div class="meta-item">
          <i class="el-icon-date"></i>
          <span>{{ article.articleDate }}</span>
        </div>

        <span class="divider">·</span>

        <div class="meta-item">
          <i class="el-icon-time"></i>
          <span>{{ readTime }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BlogHero',
  props: {
    article: {
      type: Object,
      required: true
    }
  },
  computed: {
    heroStyle() {
      if (this.article.articleCover) {
        // Handle potentially unsafe characters in URL
        const safeUrl = String(this.article.articleCover).replace(/"/g, '\"')
        return { 
          backgroundImage: `url("${safeUrl}")` 
        } 
      }
      return {
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
      }
    },
    readTime() {
      if (!this.article.articleContentHtml) return '1 分钟阅读';
      const text = this.article.articleContentHtml.replace(/<[^>]+>/g, '');
      const length = text.length;
      // Estimate: 500 chars per minute for Chinese/mixed content
      const minutes = Math.ceil(length / 500);
      return `约 ${minutes} 分钟阅读`;
    }
  }
}
</script>

<style scoped>
.blog-hero {
  position: relative;
  width: 100%;
  height: 400px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: #fff;
  display: flex;
  align-items: flex-end;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.8) 100%);
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 48px;
}

.back-btn {
  color: rgba(255,255,255,0.8);
  font-size: 16px;
  margin-bottom: 16px;
  padding-left: 0;
}
.back-btn:hover {
  color: #fff;
}

.article-title {
  font-size: 3rem;
  font-weight: 800;
  line-height: 1.2;
  margin: 0 0 24px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.article-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 15px;
  color: rgba(255,255,255,0.9);
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-avatar {
  background: rgba(255,255,255,0.2);
  color: #fff;
}

.divider {
  opacity: 0.6;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

@media (max-width: 768px) {
  .blog-hero {
    height: 300px;
  }
  .article-title {
    font-size: 2rem;
  }
  .hero-content {
    padding-bottom: 24px;
  }
}
</style>
