<template>
  <div>
    <div class="toolbar">
      <search-bar @onSearch="searchResult" ref="searchBar"></search-bar>
    </div>
    <div class="collection-grid">
      <el-tooltip effect="dark" placement="right"
                  v-for="item in movies.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                  :key="item.id"
      >
        <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.title}}</p>
        <p slot="content" style="font-size: 13px;margin-bottom: 6px">
          <span>{{item.cast}}</span> /
          <span>{{item.date}}</span> /
          <span>{{item.press}}</span>
        </p>
        <p slot="content" style="width: 300px" class="abstract">{{item.summary}}</p>
        <el-card class="collection-card"
                 bodyStyle="padding:10px" shadow="hover">
          <div class="cover">
            <img :src="item.cover" alt="封面" class="coverImage">
          </div>
          <div class="info">
            <div class="title">
              <a href="">{{item.title}}</a>
            </div>
          </div>
          <div class="collectionButton">
            <img
              v-if="checkCollection(item.id)"
              :src="imageTrue"
              alt="已收藏"
              @click="toggleFavorite(item, false)"
              class="collectionImage"
            >
            <img
              v-else
              :src="imageFalse"
              alt="未收藏"
              @click="toggleFavorite(item, true)"
              class="collectionImage"
            >
          </div>
        </el-card>
      </el-tooltip>
    </div>
    <el-row>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pagesize"
        :total="movies.length"
        class="pagination">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
import SearchBar from './SearchBar'

export default {
  name: 'AddCollectionDefault',
  components: {SearchBar},
  data () {
    return {
      addCollections: [],
      favoriteIds: [],
      movies: [],
      currentPage: 1,
      pagesize: 8,
      imageTrue: '/static/like.png',
      imageFalse: '/static/wait.png'
    }
  },
  mounted: function () {
    this.loadFavorites()
    this.loadMovies()
  },
  methods: {
    loadFavorites () {
      this.$axios.get('/me/favorites').then(resp => {
        if (resp && resp.status === 200) {
          const list = Array.isArray(resp.data) ? resp.data : []
          this.favoriteIds = list.map(m => m && m.id).filter(Boolean)
        }
      })
    },
    loadMovies () {
      var _this = this
      this.$axios.get('/movies').then(resp => {
        if (resp && resp.status === 200) {
          _this.movies = resp.data
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
            _this.movies = resp.data
          }
        })
    },
    checkCollection (movieId) {
      const id = parseInt(movieId)
      if (!id) return false
      return this.favoriteIds.includes(id)
    },
    toggleFavorite (item, shouldFavorite) {
      const id = item && item.id ? parseInt(item.id) : 0
      if (!id) return
      const title = item && item.title ? item.title : '该电影'
      const message = shouldFavorite ? `确定收藏 ${title} 吗？` : `确定取消收藏 ${title} 吗？`
      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const req = shouldFavorite
          ? this.$axios.put(`/me/favorites/${id}`)
          : this.$axios.delete(`/me/favorites/${id}`)
        req.then(resp => {
          if (resp && resp.status === 200) {
            this.loadFavorites()
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
  }

}
</script>

<style scoped>
.toolbar {
  margin: 20px;
  display: flex;
  justify-content: center;
}

.collection-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 30px;
  padding: 20px;
  justify-items: stretch;
}

.collection-grid > * {
  width: 100%;
}

.collection-card {
  width: 100%;
  height: 300px;
  border-radius: 25px;
  transition: transform 0.3s;
}

.collection-card:hover {
  transform: translateY(-5px);
}

.pagination {
  margin-top: 20px;
  text-align: center;
  margin-bottom: 20px;
}

.cover{
  width:150px;
  height:200px;
  margin-bottom: 7px;
  overflow: hidden;
  cursor: pointer;
  margin: 0 auto 7px auto;
}
.coverImage{
  width:150px;
  height:200px;
  object-fit: cover;
}
.collection{
  /* border-radius: 25px; moved to card */
}
.collectionImage{
  margin-top: 20px;
  width:30px;
  height:30px;
}
.title {
  font-size: 15px;
  text-align: center;
}

.author {
  color: #333;
  width: 102px;
  font-size: 13px;
  margin-bottom: 6px;
  text-align: left;
}

.abstract {
  display: block;
  line-height: 17px;
}

a {
  text-decoration: none;
}

a:link, a:visited, a:focus {
  color: #3377aa;
}

</style>
