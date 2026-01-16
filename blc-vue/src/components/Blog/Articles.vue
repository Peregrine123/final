
<template>
  <div class="articles-page">
    <div class="articles-area" v-loading="loading">
      <div class="page-header">
        <h1 class="page-title">博客</h1>
        <div class="page-subtitle">发现最新文章</div>
      </div>

      <div v-if="!loading && articles.length === 0" class="empty">暂无文章</div>

      <!-- Use CSS Grid to avoid float layout "holes" that can leave huge empty gaps. -->
      <div v-else class="cards-grid">
        <div
          v-for="article in articles"
          :key="article.id"
          class="card-col"
        >
          <el-card class="blog-card" shadow="never" :body-style="{ padding: '0px' }">
            <router-link class="card-cover" :to="{ name: 'Article', query: { id: article.id } }">
              <div class="cover" :style="coverStyle(article)"></div>
            </router-link>

            <div class="card-body">
              <div class="card-meta" v-if="article.articleDate">
                <i class="el-icon-date"></i>
                <span class="date">{{ article.articleDate }}</span>
              </div>

              <router-link class="card-title" :to="{ name: 'Article', query: { id: article.id } }">
                {{ article.articleTitle }}
              </router-link>

              <p class="card-abstract" v-if="article.articleAbstract">{{ article.articleAbstract }}</p>

              <div class="card-actions">
                <router-link class="read-more" :to="{ name: 'Article', query: { id: article.id } }">
                  阅读全文
                  <i class="el-icon-arrow-right"></i>
                </router-link>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <el-pagination
          v-if="total > pageSize"
          background
          layout="total, prev, pager, next, jumper"
          @current-change="handleCurrentChange"
          :page-size="pageSize"
          :total="total"
          class="pagination">
      </el-pagination>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Articles',
  data () {
    return {
      articles: [],
      pageSize: 8,
      total: 0,
      loading: false
    }
  },
  mounted () {
    this.loadArticles()
  },
  methods: {
    coverStyle (article) {
      const cover = article && article.articleCover
      if (cover) {
        // Quote url(...) to support characters like ')' in filenames.
        const safe = String(cover).replace(/"/g, '\\"')
        return { backgroundImage: `url("${safe}")` }
      }
      // Fallback placeholder for articles without covers.
      return { backgroundImage: 'linear-gradient(135deg, #e9eef6 0%, #f7f9fc 100%)' }
    },
    loadArticles () {
      this.loading = true
      this.$axios.get('/article/' + this.pageSize + '/1').then(resp => {
        if (resp && resp.status === 200) {
          this.articles = resp.data.content
          this.total = Number(resp.data.totalElements)
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleCurrentChange (page) {
      this.loading = true
      this.$axios.get('/article/' + this.pageSize + '/' + page).then(resp => {
        if (resp && resp.status === 200) {
          this.articles = resp.data.content
          this.total = Number(resp.data.totalElements)
        }
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.articles-page {
  width: 100%;
  background: #f6f7fb;
  padding: 10px 0 32px;
  min-height: calc(100vh - 64px);
}

.articles-area {
  width: 92%;
  max-width: 1240px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: #1f2328;
  letter-spacing: 0.2px;
}

.page-subtitle {
  font-size: 13px;
  color: #6b7280;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

@media (max-width: 1200px) {
  .cards-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .cards-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 600px) {
  .cards-grid {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}

.empty {
  padding: 40px 0;
  text-align: center;
  color: #6b7280;
  background: #ffffff;
  border: 1px dashed rgba(17, 24, 39, 0.18);
  border-radius: 14px;
}

/* ElementUI's internal DOM isn't affected by scoped selectors; use deep selector here. */
.blog-card >>> .el-card__body {
  display: flex;
  padding: 0 !important;
  flex-direction: column;
  height: 100%;
}

.blog-card {
  width: 100%;
  background: #fff;
  overflow: hidden;
  border-radius: 12px;
  border: 1px solid rgba(17, 24, 39, 0.08);
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
  flex: 1;
}
.blog-card:hover {
  transform: translateY(-4px);
  border-color: rgba(17, 24, 39, 0.12);
  box-shadow: 0 14px 34px rgba(17, 24, 39, 0.12);
}

.card-col {
  display: flex;
  flex-direction: column;
}

.card-cover {
  display: block;
}

.cover {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 */
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-color: #eef2f7;
}

.card-body {
  display: flex;
  flex-direction: column;
  padding: 14px 16px 16px;
  gap: 8px;
  flex: 1;
}

.card-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
}

.card-title {
  color: #1f2328;
  text-decoration: none;
  font-size: 16px;
  font-weight: 650;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-title:hover {
  color: #3b70fc;
}

.card-abstract {
  margin: 0;
  color: #4b5563;
  font-size: 13px;
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-actions {
  margin-top: auto;
  display: flex;
  justify-content: flex-end;
}
.read-more {
  color: #3b70fc;
  text-decoration: none;
  font-size: 13px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 0;
}

.read-more:hover {
  color: #2b5ae8;
}

.pagination {
  margin-top: 12px;
  text-align: center;
}
</style>
