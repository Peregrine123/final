<template>
  <div class="user-actions-container">
    <el-tabs v-model="activeTab" class="tabs">
      <el-tab-pane label="我的收藏" name="favorites">
        <div v-loading="loadingFavorites">
          <div v-if="favorites.length" class="masonry-wrapper">
            <div
              class="masonry-item"
              v-for="item in pagedFavorites"
              :key="item.id"
            >
              <div class="movie-card" @click="goToMovieDetails(item.id)">
                <div class="poster-wrapper">
                  <img :src="item.cover || defaultCover" alt="封面" class="movie-img">
                </div>
                <div class="movie-info">
                  <div class="movie-title">{{ item.title }}</div>
                  <div class="movie-year" v-if="item.date">{{ item.date }}</div>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无收藏"></el-empty>
          <el-pagination
            v-if="favorites.length > pagesize"
            @current-change="p => currentPageFavorites = p"
            :current-page="currentPageFavorites"
            :page-size="pagesize"
            :total="favorites.length"
            background
            layout="prev, pager, next"
            class="pagination"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="想看" name="wish">
        <div v-loading="loadingWish">
          <div v-if="wishMovies.length" class="masonry-wrapper">
            <div
              class="masonry-item"
              v-for="item in pagedWish"
              :key="item.id"
            >
              <div class="movie-card" @click="goToMovieDetails(item.id)">
                <div class="poster-wrapper">
                  <img :src="item.cover || defaultCover" alt="封面" class="movie-img">
                </div>
                <div class="movie-info">
                  <div class="movie-title">{{ item.title }}</div>
                  <div class="movie-year" v-if="item.date">{{ item.date }}</div>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无想看"></el-empty>
          <el-pagination
            v-if="wishMovies.length > pagesize"
            @current-change="p => currentPageWish = p"
            :current-page="currentPageWish"
            :page-size="pagesize"
            :total="wishMovies.length"
            background
            layout="prev, pager, next"
            class="pagination"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="看过" name="watched">
        <div v-loading="loadingWatched">
          <div v-if="watchedMovies.length" class="masonry-wrapper">
            <div
              class="masonry-item"
              v-for="item in pagedWatched"
              :key="item.id"
            >
              <div class="movie-card" @click="goToMovieDetails(item.id)">
                <div class="poster-wrapper">
                  <img :src="item.cover || defaultCover" alt="封面" class="movie-img">
                </div>
                <div class="movie-info">
                  <div class="movie-title">{{ item.title }}</div>
                  <div class="movie-year" v-if="item.date">{{ item.date }}</div>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无看过"></el-empty>
          <el-pagination
            v-if="watchedMovies.length > pagesize"
            @current-change="p => currentPageWatched = p"
            :current-page="currentPageWatched"
            :page-size="pagesize"
            :total="watchedMovies.length"
            background
            layout="prev, pager, next"
            class="pagination"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import defaultCover from "@/assets/remote/no-cover.svg";

export default {
  name: 'CollectionManagement',
  data() {
    return {
      defaultCover,
      activeTab: 'favorites',
      pagesize: 12,
      currentPageFavorites: 1,
      currentPageWish: 1,
      currentPageWatched: 1,
      favorites: [],
      wishMovies: [],
      watchedMovies: [],
      loadingFavorites: false,
      loadingWish: false,
      loadingWatched: false,
    }
  },
  computed: {
    pagedFavorites() {
      const start = (this.currentPageFavorites - 1) * this.pagesize
      return this.favorites.slice(start, start + this.pagesize)
    },
    pagedWish() {
      const start = (this.currentPageWish - 1) * this.pagesize
      return this.wishMovies.slice(start, start + this.pagesize)
    },
    pagedWatched() {
      const start = (this.currentPageWatched - 1) * this.pagesize
      return this.watchedMovies.slice(start, start + this.pagesize)
    },
  },
  mounted() {
    this.loadAll()
  },
  methods: {
    loadAll() {
      this.loadFavorites()
      this.loadWish()
      this.loadWatched()
    },
    loadFavorites() {
      this.loadingFavorites = true
      this.$axios.get('/me/favorites').then(resp => {
        if (resp && resp.status === 200) {
          this.favorites = Array.isArray(resp.data) ? resp.data : []
          this.currentPageFavorites = 1
        }
      }).finally(() => {
        this.loadingFavorites = false
      })
    },
    loadWish() {
      this.loadingWish = true
      this.$axios.get('/me/movie-status?status=WISH').then(resp => {
        if (resp && resp.status === 200) {
          this.wishMovies = Array.isArray(resp.data) ? resp.data : []
          this.currentPageWish = 1
        }
      }).finally(() => {
        this.loadingWish = false
      })
    },
    loadWatched() {
      this.loadingWatched = true
      this.$axios.get('/me/movie-status?status=WATCHED').then(resp => {
        if (resp && resp.status === 200) {
          this.watchedMovies = Array.isArray(resp.data) ? resp.data : []
          this.currentPageWatched = 1
        }
      }).finally(() => {
        this.loadingWatched = false
      })
    },
    goToMovieDetails(id) {
      this.$router.push({ name: 'MovieDetails', params: { id } });
    },
  }
}
</script>

<style scoped>
.user-actions-container {
  width: 90%;
  max-width: 1600px;
  margin: 0 auto;
  padding-top: 20px;
}

.tabs /deep/ .el-tabs__header {
  margin-bottom: 24px;
}

.masonry-wrapper {
  column-count: 6;
  column-gap: 20px;
}

@media (max-width: 1400px) {
  .masonry-wrapper { column-count: 5; }
}

@media (max-width: 1200px) {
  .masonry-wrapper { column-count: 4; }
}

@media (max-width: 992px) {
  .masonry-wrapper { column-count: 3; }
}

@media (max-width: 600px) {
  .masonry-wrapper { column-count: 2; }
}

.masonry-item {
  break-inside: avoid;
  margin-bottom: 24px;
}

.movie-card {
  cursor: pointer;
  transition: all 0.2s ease;
}

.movie-card:hover {
  filter: brightness(1.08);
}

.poster-wrapper {
  position: relative;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  line-height: 0;
}

.movie-img {
  width: 100%;
  height: auto;
  display: block;
  object-fit: cover;
}

.movie-info {
  padding: 10px 4px 0 4px;
}

.movie-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
  margin-bottom: 4px;
}

.movie-year {
  font-size: 13px;
  color: #888;
}

.pagination {
  margin-top: 30px;
  margin-bottom: 30px;
  text-align: center;
}
</style>
