<template>
  <div class="article-page-redesign" v-loading="loading">
    <BlogHero :article="article" @back="goBack" />

	    <div class="content-container">
	      <div class="main-column">
	         <BlogContent
	            :content="article.articleContentHtml"
	            @headers-parsed="handleHeaders"
	         />

	         <!-- Bottom Actions -->
	         <div class="article-footer">
	            <div class="action-buttons">
	               <el-button type="primary" icon="el-icon-thumb" circle plain></el-button>
               <el-button type="warning" icon="el-icon-star-off" circle plain></el-button>
               <el-button icon="el-icon-share" circle plain></el-button>
            </div>
            <div class="footer-nav">
               <el-button @click="goBack">返回博客列表</el-button>
            </div>
         </div>
      </div>

      <div class="sidebar-column">
         <BlogSidebar>
            <template #toc>
               <BlogTOC :headers="headers" />
            </template>
         </BlogSidebar>
      </div>
    </div>
  </div>
</template>

<script>
import BlogHero from './article/BlogHero'
import BlogContent from './article/BlogContent'
import BlogTOC from './article/BlogTOC'
import BlogSidebar from './article/BlogSidebar'

export default {
  name: 'ArticleDetails',
  components: {
    BlogHero,
    BlogContent,
    BlogTOC,
    BlogSidebar
  },
  data () {
    return {
      article: {},
      loading: false,
      headers: []
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
    },
    handleHeaders(headers) {
      this.headers = headers;
    }
  }
}
</script>

<style scoped>
.article-page-redesign {
  min-height: 100vh;
  background: #fff;
  padding-bottom: 60px;
}

.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 40px;
}

.main-column {
  min-width: 0; /* Prevent grid blowout */
}

.article-footer {
  margin-top: 60px;
  padding-top: 40px;
  border-top: 1px solid #f1f5f9;
  text-align: center;
}

.action-buttons {
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
  gap: 16px;
}

.action-buttons .el-button {
  font-size: 20px;
  padding: 12px;
}

@media (max-width: 1024px) {
  .content-container {
    grid-template-columns: 1fr;
  }
  .sidebar-column {
      order: 2;
  }
}
</style>
