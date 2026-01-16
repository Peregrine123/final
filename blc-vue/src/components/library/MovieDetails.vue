<template>
  <div class="movie-details-container" v-loading="loading">
    <!-- Hero Section / Backdrop -->
    <div class="hero-section" :style="heroStyle">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <el-row type="flex" justify="center" align="middle" class="hero-inner">
          <!-- Poster -->
          <el-col :xs="24" :sm="8" :md="6" class="poster-col">
            <div class="poster-wrapper">
              <img :src="movie.cover || defaultCover" alt="Poster" class="poster-img">
            </div>
          </el-col>
          
          <!-- Hero Info -->
          <el-col :xs="24" :sm="16" :md="14" class="info-col">
            <h1 class="movie-title text-white">{{ movie.title }}</h1>
            <div class="movie-meta text-gray-light">
              <span>{{ movie.date ? movie.date.substring(0, 4) : 'Year' }}</span>
              <span class="divider">•</span>
              <span>{{ mockData.genre }}</span>
              <span class="divider">•</span>
              <span>{{ mockData.duration }}</span>
            </div>
            
            <div class="rating-section">
              <el-rate
                v-model="mockData.rating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}">
              </el-rate>
              <span class="rating-count">({{ mockData.ratingCount }} 人评价)</span>
            </div>

            <div class="action-buttons">
              <el-button type="primary" icon="el-icon-edit">写影评</el-button>
              <el-button type="warning" icon="el-icon-star-off">收藏</el-button>
              <el-button plain icon="el-icon-share">分享</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- Main Content Area -->
    <div class="main-content-wrapper">
      <el-row :gutter="40">
        <!-- Left: Synopsis & Cast & Reviews -->
        <el-col :xs="24" :sm="24" :md="16">
          <section class="section-block">
            <h2 class="section-title">剧情简介</h2>
            <p class="synopsis-text">
              {{ movie.summary || '暂无简介' }}
            </p>
          </section>

          <section class="section-block">
            <h2 class="section-title">演职员表</h2>
            <div class="cast-list">
              <!-- Using real cast data if available, otherwise mock strictly for UI demo -->
              <el-card shadow="hover" class="cast-card">
                <div class="cast-info">
                   <el-avatar :size="60" icon="el-icon-user-solid"></el-avatar>
                   <div class="cast-name">{{ movie.cast || '未知' }}</div>
                   <div class="cast-role">导演 / 主演</div>
                </div>
              </el-card>
            </div>
          </section>

          <section class="section-block">
             <h2 class="section-title">短评</h2>
             <div class="reviews-list">
               <div v-for="review in mockData.reviews" :key="review.id" class="review-item">
                 <div class="review-header">
                   <span class="review-author">{{ review.author }}</span>
                   <el-rate v-model="review.rating" disabled text-color="#ff9900" class="mini-rate"></el-rate>
                   <span class="review-date">{{ review.date }}</span>
                 </div>
                 <p class="review-content">{{ review.content }}</p>
               </div>
             </div>
          </section>
        </el-col>

        <!-- Right: Sidebar Details -->
        <el-col :xs="24" :sm="24" :md="8">
          <el-card class="sidebar-card" shadow="never">
            <div slot="header" class="clearfix">
              <span>详细信息</span>
            </div>
            <div class="detail-item">
              <span class="label">导演/主演:</span>
              <span class="value">{{ movie.cast }}</span>
            </div>
            <div class="detail-item">
              <span class="label">发行商:</span>
              <span class="value">{{ movie.press }}</span>
            </div>
            <div class="detail-item">
              <span class="label">上映日期:</span>
              <span class="value">{{ movie.date }}</span>
            </div>
          </el-card>

          <el-card class="sidebar-card" shadow="never" style="margin-top: 20px;">
            <div slot="header">
              <span>相关推荐</span>
            </div>
            <div v-if="relatedMovies.length" class="related-movies">
              <div
                v-for="m in relatedMovies"
                :key="m.id"
                class="related-item"
                @click="goToMovieDetails(m.id)"
              >
                <img :src="m.cover || defaultCover" alt="Poster" class="related-poster">
                <div class="related-info">
                  <div class="related-title">{{ m.title }}</div>
                  <div class="related-meta">{{ m.date ? m.date.substring(0, 4) : '' }}</div>
                </div>
              </div>
            </div>
            <div v-else class="related-empty">暂无相关推荐</div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import defaultCover from "@/assets/remote/no-cover.svg";

export default {
  name: 'MovieDetails',
  data() {
    return {
      movie: {},
      relatedMovies: [],
      loading: true,
      defaultCover,
      // Mock data for UI elements not supported by backend
      mockData: {
        rating: 4.5,
        ratingCount: 1234,
        genre: '剧情 / 爱情',
        duration: '120分钟',
        reviews: [
          { id: 1, author: 'Alice', rating: 5, date: '2023-10-01', content: '这部电影太棒了！画面精美，剧情感人。' },
          { id: 2, author: 'Bob', rating: 4, date: '2023-10-05', content: '整体不错，但是结局有点仓促。' }
        ]
      }
    };
  },
  computed: {
    heroStyle() {
      // Use cover as background or a default dark color
      const url = this.movie.cover || '';
      const safe = String(url).replace(/\"/g, '\\\"');
      return {
        backgroundImage: url ? `url(\"${safe}\")` : 'linear-gradient(135deg, #111827 0%, #374151 100%)',
        backgroundSize: 'cover',
        backgroundPosition: 'center top'
      };
    }
  },
  created() {
    this.loadMovieDetails();
  },
  watch: {
    '$route.params.id': function () {
      this.loadMovieDetails();
    }
  },
  methods: {
    loadMovieDetails() {
      const id = parseInt(this.$route.params.id); // Ensure ID type matches
      this.loading = true;
      this.relatedMovies = [];
      this.$axios.get('/movies').then(resp => {
        if (resp && resp.status === 200) {
          const movies = resp.data;
          // Find the movie by ID. 
          // Note: Assuming API returns a list and we filter client-side as per original code.
          // Original code used a loop, using find here is cleaner.
          // Using == to match string/number loose types just in case.
          const found = movies.find(m => m.id == id);
          if (found) {
            this.movie = found;
            // Generate deterministic mock rating based on ID for consistency
            this.mockData.rating = (id % 5) + 1; // Random-ish rating 1-5
            this.relatedMovies = this.buildRelatedMovies(movies, found, 3);
          } else {
            this.$message.error('未找到该电影信息');
          }
        }
        this.loading = false;
      }).catch(err => {
        console.error(err);
        this.loading = false;
        this.$message.error('加载失败');
      });
    },
    buildRelatedMovies(allMovies, currentMovie, limit) {
      const safeLimit = Math.max(0, parseInt(limit) || 0);
      if (!Array.isArray(allMovies) || !currentMovie || !safeLimit) return [];

      const currentId = currentMovie.id;
      const cid = currentMovie.category && currentMovie.category.id;

      const withCategory = Array.isArray(allMovies) ? allMovies : [];
      const candidates = cid
        ? withCategory.filter(m => m && m.category && m.category.id === cid)
        : withCategory.slice();

      if (candidates.length <= 1) return [];

      // Stable order by id so "附近" has a clear meaning.
      candidates.sort((a, b) => (a.id || 0) - (b.id || 0));

      const idx = candidates.findIndex(m => m && m.id == currentId);
      const prev = idx > 0 ? candidates.slice(0, idx).reverse() : [];
      const next = idx >= 0 ? candidates.slice(idx + 1) : candidates.filter(m => m && m.id != currentId);

      const related = [];
      for (let i = 0; related.length < safeLimit && (i < next.length || i < prev.length); i++) {
        if (i < next.length && next[i] && next[i].id != currentId) related.push(next[i]);
        if (related.length >= safeLimit) break;
        if (i < prev.length && prev[i] && prev[i].id != currentId) related.push(prev[i]);
      }
      return related.slice(0, safeLimit);
    },
    goToMovieDetails(id) {
      if (!id && id !== 0) return;
      this.$router.push({ name: 'MovieDetails', params: { id } });
    }
  }
};
</script>

<style scoped>
/* Reset & Base */
.movie-details-container {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  color: #333;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* Hero Section */
.hero-section {
  position: relative;
  width: 100%;
  height: 400px; /* Fixed height for hero */
  overflow: hidden;
  margin-bottom: 40px;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(30, 30, 30, 0.85); /* Dark overlay */
  backdrop-filter: blur(20px); /* Modern blur */
  /* Fallback for older browsers if needed, though most support backdrop-filter now or we use the image blur trick */
}

.hero-content {
  position: relative;
  z-index: 2;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-inner {
  height: 100%;
}

/* Poster */
.poster-wrapper {
  text-align: center;
  margin-top: 60px; /* Push down to overlap bottom slightly or center */
}

.poster-img {
  width: 200px;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 10px 20px rgba(0,0,0,0.3);
  border: 4px solid #fff;
}

/* Hero Info */
.info-col {
  text-align: left;
  padding-left: 40px;
}

.movie-title {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 700;
}

.text-white { color: #fff; }
.text-gray-light { color: #ccc; }

.movie-meta {
  font-size: 1rem;
  margin-bottom: 20px;
}

.divider {
  margin: 0 10px;
}

.rating-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.rating-count {
  margin-left: 10px;
  color: #ccc;
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

/* Main Content */
.main-content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  padding-bottom: 60px;
}

.section-block {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 30px;
  text-align: left; /* Ensure text alignment is left */
}

.section-title {
  font-size: 1.5rem;
  margin-bottom: 20px;
  border-left: 5px solid #409EFF;
  padding-left: 15px;
  line-height: 1.2;
}

.synopsis-text {
  line-height: 1.8;
  font-size: 1.1rem;
  color: #555;
  text-indent: 2em;
}

/* Cast */
.cast-list {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.cast-card {
  width: 150px;
  text-align: center;
}

.cast-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.cast-name {
  margin-top: 10px;
  font-weight: bold;
  font-size: 0.9rem;
}

.cast-role {
  font-size: 0.8rem;
  color: #888;
}

/* Reviews */
.reviews-list .review-item {
  border-bottom: 1px solid #eee;
  padding: 15px 0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.review-author {
  font-weight: bold;
  margin-right: 15px;
}

.review-date {
  margin-left: auto;
  color: #999;
  font-size: 0.85rem;
}

.review-content {
  color: #666;
  line-height: 1.6;
}

/* Sidebar */
.sidebar-card {
  text-align: left;
}
.detail-item {
  margin-bottom: 12px;
  font-size: 0.95rem;
}

.detail-item .label {
  color: #888;
  margin-right: 10px;
}

.detail-item .value {
  color: #333;
}

/* Related */
.related-item {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
  cursor: pointer;
}

.related-poster {
  width: 50px;
  height: 75px;
  margin-right: 15px;
  border-radius: 4px;
  object-fit: cover;
  background-color: #eee;
}

.related-empty {
  color: #999;
  font-size: 0.9rem;
  padding: 8px 0;
}

.related-title {
  font-size: 0.95rem;
  font-weight: bold;
  margin-bottom: 5px;
}

.related-meta {
  color: #999;
  font-size: 0.8rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .hero-section {
    height: auto;
    padding-bottom: 40px;
  }
  
  .poster-wrapper {
    margin-top: 20px;
  }
  
  .info-col {
    padding-left: 0;
    text-align: center;
    margin-top: 20px;
  }
  
  .rating-section {
    justify-content: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
}
</style>
