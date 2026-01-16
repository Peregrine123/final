<template>
  <div class="article-page">
    <div v-if="article.articleCover" class="hero" :style="heroStyle">
      <div class="hero-mask">
        <div class="hero-inner">
          <el-button class="back-btn" type="text" icon="el-icon-arrow-left" @click="goBack">返回列表</el-button>
          <h1 class="hero-title">{{ article.articleTitle }}</h1>
          <div class="hero-meta" v-if="article.articleDate">
            <i class="el-icon-date"></i>
            <span>{{ article.articleDate }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="content-wrap" :class="{ 'with-hero': !!article.articleCover }">
      <div v-if="!article.articleCover" class="plain-header">
        <el-button class="back-btn" type="text" icon="el-icon-arrow-left" @click="goBack">返回列表</el-button>
        <h1 class="plain-title">{{ article.articleTitle }}</h1>
        <div class="plain-meta" v-if="article.articleDate">
          <i class="el-icon-date"></i>
          <span>{{ article.articleDate }}</span>
        </div>
      </div>

      <el-card class="article-card" shadow="never" v-loading="loading">
        <div class="markdown-body" v-html="article.articleContentHtml"></div>
      </el-card>

      <div class="bottom-actions">
        <el-button @click="goBack">返回博客列表</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ArticleDetails',
  data () {
    return {
      article: {},
      loading: false
    }
  },
  computed: {
    heroStyle () {
      const cover = this.article && this.article.articleCover
      if (!cover) return {}
      // Quote url(...) to support characters like ')' in filenames.
      const safe = String(cover).replace(/"/g, '\\"')
      return { backgroundImage: `url("${safe}")` }
    }
  },
  mounted () {
    this.loadArticle()
  },
  methods: {
    goBack () {
      this.$router.push({ path: '/blog' })
    },
    loadArticle () {
      this.loading = true
      this.$axios.get('/article/' + this.$route.query.id).then(resp => {
        if (resp && resp.status === 200) {
          this.article = resp.data
        }
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.article-page {
  min-height: 100%;
  background: #f6f7fb;
  padding-bottom: 48px;
}

.hero {
  position: relative;
  height: 320px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.hero-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(15, 23, 42, 0.2) 0%, rgba(15, 23, 42, 0.75) 100%);
}

.hero-inner {
  height: 100%;
  width: 92%;
  max-width: 980px;
  margin: 0 auto;
  padding: 24px 0 28px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 10px;
}

.back-btn {
  align-self: flex-start;
  padding-left: 0;
  padding-right: 0;
}

.hero .back-btn {
  color: rgba(255, 255, 255, 0.9);
}
.hero .back-btn:hover {
  color: #ffffff;
}

.hero-title {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: 0.2px;
  line-height: 1.2;
  text-shadow: 0 12px 30px rgba(0, 0, 0, 0.35);
}

.hero-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
}

.content-wrap {
  width: 92%;
  max-width: 980px;
  margin: 24px auto 0;
}

.content-wrap.with-hero {
  margin-top: -56px;
}

.plain-header {
  margin: 0 0 14px;
}

.plain-title {
  margin: 6px 0 6px;
  font-size: 26px;
  font-weight: 800;
  color: #1f2328;
  line-height: 1.25;
}

.plain-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
}

.article-card {
  border-radius: 14px;
  border: 1px solid rgba(17, 24, 39, 0.08);
  overflow: hidden;
}

.article-card >>> .el-card__body {
  padding: 28px 28px 34px;
}

.markdown-body {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  font-size: 17px;
  line-height: 1.85;
  color: #1f2328;
  word-break: break-word;
}

/* Typographic defaults for v-html content (needs deep selector with scoped CSS). */
.markdown-body >>> p {
  margin: 0 0 1.2em;
}

.markdown-body >>> a {
  color: #3b70fc;
  text-decoration: none;
  border-bottom: 1px solid rgba(59, 112, 252, 0.28);
  transition: color 0.15s ease, border-color 0.15s ease;
}
.markdown-body >>> a:hover {
  color: #2b5ae8;
  border-bottom-color: rgba(43, 90, 232, 0.45);
}

.markdown-body >>> h1,
.markdown-body >>> h2,
.markdown-body >>> h3,
.markdown-body >>> h4 {
  margin: 1.6em 0 0.8em;
  line-height: 1.3;
  color: #111827;
}

.markdown-body >>> h1 {
  font-size: 1.7em;
}

.markdown-body >>> h2 {
  font-size: 1.4em;
}

.markdown-body >>> h3 {
  font-size: 1.2em;
}

.markdown-body >>> ul,
.markdown-body >>> ol {
  padding-left: 1.25em;
  margin: 0 0 1.2em;
}

.markdown-body >>> li {
  margin: 0.35em 0;
}

.markdown-body >>> blockquote {
  margin: 1.2em 0;
  padding: 0.9em 1em;
  border-left: 4px solid #3b70fc;
  background: rgba(59, 112, 252, 0.07);
  color: #334155;
  border-radius: 8px;
}

.markdown-body >>> hr {
  border: 0;
  border-top: 1px solid rgba(17, 24, 39, 0.12);
  margin: 2em 0;
}

.markdown-body >>> pre {
  margin: 1.1em 0 1.4em;
  padding: 14px 16px;
  background: #0b1020;
  color: #e6edf3;
  border-radius: 12px;
  overflow: auto;
}

.markdown-body >>> code {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 0.95em;
  background: rgba(175, 184, 193, 0.22);
  padding: 0.15em 0.35em;
  border-radius: 6px;
}

.markdown-body >>> pre code {
  background: transparent;
  padding: 0;
  border-radius: 0;
  color: inherit;
}

/* Ensure images in markdown content are responsive and look consistent. */
.markdown-body >>> img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 1em auto;
  border-radius: 12px;
  box-shadow: 0 10px 24px rgba(17, 24, 39, 0.12);
}

.markdown-body >>> table {
  width: 100%;
  border-collapse: collapse;
  margin: 1em 0 1.4em;
  overflow: hidden;
  font-size: 0.95em;
}

.markdown-body >>> th,
.markdown-body >>> td {
  border: 1px solid rgba(17, 24, 39, 0.12);
  padding: 8px 10px;
}

.markdown-body >>> th {
  text-align: left;
  background: rgba(17, 24, 39, 0.04);
  color: #111827;
}

.bottom-actions {
  margin-top: 14px;
  text-align: center;
}

@media (max-width: 768px) {
  .hero {
    height: 240px;
  }
  .hero-title {
    font-size: 22px;
  }
  .content-wrap {
    margin-top: 18px;
  }
  .content-wrap.with-hero {
    margin-top: -34px;
  }
  .article-card >>> .el-card__body {
    padding: 18px 16px 22px;
  }
  .markdown-body {
    font-size: 16px;
  }
}
</style>
