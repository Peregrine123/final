<template>
  <div class="movies-container">
	    <el-row style="margin-bottom: 30px;">
	      <search-bar @onSearch="searchResult" ref="searchBar"></search-bar>
	    </el-row>

	    <div class="masonry-wrapper">
	      <div class="masonry-item"
	           v-for="item in movies.slice((currentPage-1)*pagesize,currentPage*pagesize)"
	           :key="item.id">

	        <div class="movie-card" @click="goToMovieDetails(item.id)">
	           <div class="poster-wrapper">
	             <img :src="item.cover" alt="封面" class="movie-img">
	           </div>
           <div class="movie-info">
             <div class="movie-title">{{item.title}}</div>
             <div class="movie-year" v-if="item.date">{{item.date}}</div>
           </div>
        </div>
      </div>
    </div>

    <el-row type="flex" justify="center" style="margin-top: 40px; margin-bottom: 40px;">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pagesize"
          :total="movies.length"
          background
          layout="prev, pager, next">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
import SearchBar from './SearchBar'
import { dedupeMovies } from '@/utils/dedupe'
export default {
  name: 'MoviesDefault',
  components: {SearchBar},
  data () {
    return {
      movies: [],
      currentPage: 1,
      pagesize: 12,
    }
  },
  mounted: function () {
    this.loadMovies()
  },
  methods: {
    loadMovies () {
      var _this = this
      this.$axios.get('/movies').then(resp => {
        if (resp && resp.status === 200) {
          _this.movies = dedupeMovies(resp.data)
          _this.currentPage = 1
        }
      })
    },
    handleCurrentChange: function (currentPage) {
      this.currentPage = currentPage
    },
    searchResult () {
      var _this = this
      this.$axios
          .get('/search?keywords=' + this.$refs.searchBar.keywords, {
          }).then(resp => {
        if (resp && resp.status === 200) {
          _this.movies = dedupeMovies(resp.data)
          _this.currentPage = 1
        }
      })
    },
    goToMovieDetails(id) {
      this.$router.push({ name: 'MovieDetails', params: { id } });
    },
  }
}
</script>

<style scoped>
.movies-container {
  width: 90%;
  max-width: 1600px;
  margin: 0 auto;
  padding-top: 30px;
}

.masonry-wrapper {
  column-count: 6;
  column-gap: 20px;
}

@media (max-width: 1400px) {
  .masonry-wrapper {
    column-count: 5;
  }
}

@media (max-width: 1200px) {
  .masonry-wrapper {
    column-count: 4;
  }
}

@media (max-width: 992px) {
  .masonry-wrapper {
    column-count: 3;
  }
}

@media (max-width: 600px) {
  .masonry-wrapper {
    column-count: 2;
  }
}

.masonry-item {
  break-inside: avoid;
  margin-bottom: 24px;
}

.movie-card {
  cursor: pointer;
  transition: all 0.2s ease;
}

/* Subtle hover effect: brightness, NO movement */
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
</style>
