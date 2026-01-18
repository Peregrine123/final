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
              <span>{{ movie.category ? movie.category.name : mockData.genre }}</span>
              <span class="divider">•</span>
              <span>{{ mockData.duration }}</span>
            </div>
            
            <div class="rating-section">
              <el-rate
                :value="ratingSummaryDisplay"
                disabled
                :show-score="ratingSummary.count > 0"
                allow-half
                text-color="#ff9900"
                score-template="{value}">
              </el-rate>
              <span v-if="ratingSummary.count > 0" class="rating-count">
                ({{ ratingSummary.count }} 人评价)
              </span>
              <span v-else class="rating-count">(暂无评分)</span>
            </div>

            <div class="action-buttons">
              <el-button
                v-if="isAdmin"
                type="primary"
                icon="el-icon-edit"
                @click="goToWriteReview"
              >写影评</el-button>
              <el-button
                :type="watchStatus === 'WISH' ? 'success' : 'default'"
                icon="el-icon-time"
                :loading="watchStatusLoading"
                @click="setWatchStatus('WISH')"
              >想看</el-button>
              <el-button
                :type="watchStatus === 'WATCHED' ? 'success' : 'default'"
                icon="el-icon-circle-check"
                :loading="watchStatusLoading"
                @click="setWatchStatus('WATCHED')"
              >看过</el-button>
              <el-button
                type="warning"
                :icon="favorited ? 'el-icon-star-on' : 'el-icon-star-off'"
                :loading="favoriteLoading"
                @click="toggleFavorite"
              >{{ favorited ? '已收藏' : '收藏' }}</el-button>
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
             <h2 class="section-title">我的短评</h2>
             <div class="my-comment">
               <div class="my-comment-row">
                 <span class="my-comment-label">评分</span>
                 <el-rate
                   v-model="myCommentForm.rating"
                   :max="5"
                   text-color="#ff9900"
                   show-score>
                 </el-rate>
               </div>

               <el-input
                 v-model="myCommentForm.content"
                 type="textarea"
                 :rows="3"
                 maxlength="280"
                 show-word-limit
                 placeholder="写下你的短评（可只评分或只写短评）"
               />

               <div class="my-comment-actions">
                 <el-button
                   type="primary"
                   :loading="myCommentSubmitting"
                   @click="submitMyComment"
                 >提交</el-button>
                 <span v-if="myComment && myComment.updatedAt" class="my-comment-tip">
                   更新于：{{ formatDate(myComment.updatedAt) }}
                 </span>
               </div>
             </div>

             <h2 class="section-title" style="margin-top: 30px;">大家的短评</h2>
             <div class="reviews-list" v-loading="commentsLoading">
               <div v-if="comments.items.length === 0" class="review-empty">暂无短评</div>
               <div v-for="c in comments.items" :key="c.id" class="review-item">
                 <div class="review-header">
                   <span class="review-author">{{ c.username || '匿名' }}</span>
                   <el-rate
                     v-if="c.rating"
                     v-model="c.rating"
                     disabled
                     text-color="#ff9900"
                     class="mini-rate"
                   />
                   <span class="review-date">{{ formatDate(c.createdAt) }}</span>
                   <el-button
                     size="mini"
                     :type="c.likedByMe ? 'primary' : 'text'"
                     :loading="!!c.likeLoading"
                     class="like-btn"
                     @click="toggleLike(c)"
                   >{{ c.likedByMe ? '已赞' : '点赞' }} {{ c.likeCount || 0 }}</el-button>
                 </div>
                 <p class="review-content">{{ c.content || '' }}</p>
               </div>
             </div>

             <div v-if="comments.total > comments.size" class="reviews-pagination">
               <el-pagination
                 layout="prev, pager, next"
                 :page-size="comments.size"
                 :total="comments.total"
                 :current-page="comments.page"
                 @current-change="handleCommentsPageChange"
               />
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
import { dedupeMovies } from '@/utils/dedupe'

export default {
  name: 'MovieDetails',
  data() {
    return {
      movie: {},
      relatedMovies: [],
      loading: true,
      defaultCover,
      role: '',
      favorited: false,
      favoriteLoading: false,
      watchStatus: null, // 'WISH' | 'WATCHED' | null
      watchStatusLoading: false,
      // UI-only placeholders (not provided by backend in this project)
      mockData: {
        genre: '剧情 / 爱情',
        duration: '120分钟'
      },
      ratingSummary: {
        average: null,
        count: 0
      },
      myComment: null,
      myCommentForm: {
        rating: 0,
        content: ''
      },
      myCommentSubmitting: false,
      commentsLoading: false,
      comments: {
        items: [],
        total: 0,
        page: 1,
        size: 10
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
    },
    ratingSummaryDisplay() {
      const avg = this.ratingSummary && this.ratingSummary.average != null ? Number(this.ratingSummary.average) : 0;
      // el-rate with allow-half displays half steps; round for a stable UI.
      const rounded = Math.round(avg * 2) / 2;
      return isNaN(rounded) ? 0 : rounded;
    },
    isAdmin() {
      return this.role === 'admin'
    }
  },
  created() {
    this.loadMovieDetails();
    this.loadRole();
  },
  watch: {
    '$route.params.id': function () {
      this.loadMovieDetails();
    }
  },
  methods: {
    loadRole() {
      const userStr = window.localStorage.getItem('user')
      let username = ''
      try {
        username = userStr ? (JSON.parse(userStr).username || '') : ''
      } catch (e) {
        username = ''
      }
      if (!username) {
        this.role = ''
        return
      }
      this.$axios.get('/user/role?username=' + username)
        .then(resp => {
          if (resp && resp.status === 200 && resp.data && resp.data.code === 200) {
            this.role = resp.data.result
          } else {
            this.role = ''
          }
        })
        .catch(() => {
          this.role = ''
        })
    },
    goToWriteReview() {
      if (!this.isAdmin) return
      const m = this.movie || {}
      const title = m.title ? `《${m.title}》影评` : '影评'
      const abstract = m.summary || ''
      const cover = m.cover || ''
      const md = (abstract ? `## 影片简介\n\n${abstract}\n\n` : '') + '## 影评\n\n'

      this.$router.push({
        name: 'ArticleEditor',
        params: {
          article: {
            id: '',
            articleTitle: title,
            articleAbstract: abstract,
            articleCover: cover,
            articleContentMd: md,
            articleContentHtml: '',
            articleDate: ''
          }
        }
      })
    },
    loadMovieDetails() {
      const id = parseInt(this.$route.params.id); // Ensure ID type matches
      this.loading = true;
      this.relatedMovies = [];
      this.$axios.get('/movies').then(resp => {
        if (resp && resp.status === 200) {
          const movies = dedupeMovies(resp.data);
          // Find the movie by ID. 
          // Note: Assuming API returns a list and we filter client-side as per original code.
          // Original code used a loop, using find here is cleaner.
          // Using == to match string/number loose types just in case.
          const found = movies.find(m => m.id == id);
          if (found) {
            this.movie = found;
            this.loadUserActions(found.id);
            this.loadRatingSummary(found.id);
            this.loadMyComment(found.id);
            this.comments.page = 1;
            this.comments.items = [];
            this.comments.total = 0;
            this.loadApprovedComments(found.id);
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
    loadUserActions(movieId) {
      const mid = parseInt(movieId);
      if (!mid) return;
      // Load in parallel; if not logged in, global interceptor will redirect.
      this.favoriteLoading = true;
      this.watchStatusLoading = true;
      Promise.all([
        this.$axios.get(`/me/favorites/${mid}`),
        this.$axios.get(`/me/movie-status/${mid}`)
      ]).then(([favResp, statusResp]) => {
        if (favResp && favResp.status === 200) {
          this.favorited = !!(favResp.data && favResp.data.favorited);
        }
        if (statusResp && statusResp.status === 200) {
          this.watchStatus = statusResp.data ? statusResp.data.status : null;
        }
      }).catch(err => {
        console.error(err);
      }).finally(() => {
        this.favoriteLoading = false;
        this.watchStatusLoading = false;
      });
    },
    loadRatingSummary(movieId) {
      const mid = parseInt(movieId);
      if (!mid) return;
      this.$axios.get(`/movies/${mid}/rating-summary`).then(resp => {
        if (resp && resp.status === 200) {
          this.ratingSummary = resp.data || { average: null, count: 0 };
        }
      }).catch(err => {
        console.error(err);
      });
    },
    loadMyComment(movieId) {
      const mid = parseInt(movieId);
      if (!mid) return;
      this.$axios.get(`/me/movie-comments/${mid}`).then(resp => {
        if (resp && resp.status === 200) {
          this.myComment = resp.data;
          this.myCommentForm.rating = this.myComment && this.myComment.rating ? this.myComment.rating : 0;
          this.myCommentForm.content = this.myComment && this.myComment.content ? this.myComment.content : '';
        }
      }).catch(err => {
        // 404 means "no comment yet"
        if (err && err.response && err.response.status === 404) {
          this.myComment = null;
          this.myCommentForm.rating = 0;
          this.myCommentForm.content = '';
          return;
        }
        console.error(err);
      });
    },
    loadApprovedComments(movieId) {
      const mid = parseInt(movieId);
      if (!mid) return;
      this.commentsLoading = true;
      const page = this.comments.page || 1;
      const size = this.comments.size || 10;
      this.$axios.get(`/movies/${mid}/comments?page=${page}&size=${size}`).then(resp => {
        if (resp && resp.status === 200) {
          const data = resp.data || {};
          this.comments.items = Array.isArray(data.items) ? data.items : [];
          this.comments.total = data.total || 0;
          this.comments.page = data.page || page;
          this.comments.size = data.size || size;
        }
      }).catch(err => {
        console.error(err);
      }).finally(() => {
        this.commentsLoading = false;
      });
    },
    handleCommentsPageChange(page) {
      this.comments.page = page;
      this.loadApprovedComments(this.movie && this.movie.id);
    },
    submitMyComment() {
      const mid = this.movie && this.movie.id ? parseInt(this.movie.id) : 0;
      if (!mid) return;

      const ratingNum = Number(this.myCommentForm.rating || 0);
      const rating = ratingNum > 0 ? ratingNum : null;
      const content = this.myCommentForm.content;

      this.myCommentSubmitting = true;
      this.$axios.put(`/me/movie-comments/${mid}`, { rating, content }).then(resp => {
        if (resp && resp.status === 200) {
          this.myComment = resp.data;
          this.myCommentForm.rating = this.myComment && this.myComment.rating ? this.myComment.rating : 0;
          this.myCommentForm.content = this.myComment && this.myComment.content ? this.myComment.content : '';
          this.watchStatus = 'WATCHED';
          this.$message.success('发布成功');
          this.loadRatingSummary(mid);
          this.comments.page = 1;
          this.loadApprovedComments(mid);
        }
      }).catch(err => {
        console.error(err);
        const msg = err && err.response && err.response.data && err.response.data.message
          ? String(err.response.data.message)
          : '提交失败';
        this.$message.error(msg);
      }).finally(() => {
        this.myCommentSubmitting = false;
      });
    },
    toggleLike(comment) {
      if (!comment || !comment.id) return;
      const cid = parseInt(comment.id);
      if (!cid) return;

      this.$set(comment, 'likeLoading', true);
      const req = comment.likedByMe
        ? this.$axios.delete(`/me/movie-comment-likes/${cid}`)
        : this.$axios.put(`/me/movie-comment-likes/${cid}`);

      req.then(resp => {
        if (resp && resp.status === 200) {
          comment.likedByMe = !!(resp.data && resp.data.liked);
          comment.likeCount = resp.data && typeof resp.data.likeCount === 'number' ? resp.data.likeCount : (comment.likeCount || 0);
        }
      }).catch(err => {
        console.error(err);
        this.$message.error('操作失败');
      }).finally(() => {
        this.$set(comment, 'likeLoading', false);
      });
    },
    formatDate(iso) {
      if (!iso) return '';
      const d = new Date(iso);
      if (isNaN(d.getTime())) return String(iso);
      const yyyy = d.getFullYear();
      const mm = String(d.getMonth() + 1).padStart(2, '0');
      const dd = String(d.getDate()).padStart(2, '0');
      return `${yyyy}-${mm}-${dd}`;
    },
    toggleFavorite() {
      const mid = this.movie && this.movie.id ? parseInt(this.movie.id) : 0;
      if (!mid) return;
      this.favoriteLoading = true;
      const req = this.favorited
        ? this.$axios.delete(`/me/favorites/${mid}`)
        : this.$axios.put(`/me/favorites/${mid}`);
      req.then(resp => {
        if (resp && resp.status === 200) {
          this.favorited = !!(resp.data && resp.data.favorited);
        }
      }).catch(err => {
        console.error(err);
        this.$message.error('操作失败');
      }).finally(() => {
        this.favoriteLoading = false;
      });
    },
    setWatchStatus(status) {
      const mid = this.movie && this.movie.id ? parseInt(this.movie.id) : 0;
      if (!mid) return;
      this.watchStatusLoading = true;
      this.$axios.put(`/me/movie-status/${mid}`, { status }).then(resp => {
        if (resp && resp.status === 200) {
          this.watchStatus = resp.data ? resp.data.status : status;
        }
      }).catch(err => {
        console.error(err);
        this.$message.error('操作失败');
      }).finally(() => {
        this.watchStatusLoading = false;
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

.like-btn {
  margin-left: 8px;
}

.my-comment-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.my-comment-label {
  width: 48px;
  color: #666;
}

.my-comment-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
}

.my-comment-tip {
  color: #999;
  font-size: 0.85rem;
}

.review-empty {
  color: #999;
  padding: 12px 0;
}

.reviews-pagination {
  margin-top: 12px;
  display: flex;
  justify-content: center;
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
