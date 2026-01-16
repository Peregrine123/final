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
            <img v-if="checkCollection(item.title)" :src="imageTrue" alt="选择" class="collectionImage">
            <img v-else :src="imageFalse" alt="未选择" @click="chooseCollection(item)" class="collectionImage">
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
      collections: [],
      movies: [],
      currentPage: 1,
      pagesize: 8,
      imageTrue: '/static/like.png',
      imageFalse: '/static/wait.png'
    }
  },
  mounted: function () {
    this.loadCollections()
    this.loadMovies()
  },
  methods: {
    loadCollections () {
      var _this = this
      this.$axios.get('/collections').then(resp => {
        if (resp && resp.status === 200) {
          _this.collections = resp.data
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
    checkCollection (title) {
      var result = false
      try {
        this.collections.forEach(item => {
          if (item.title === title) {
            result = true
            throw (result)
          }
        })
      } catch (e) {
        return e
      }
      return result
    },
    chooseCollection (item) {
      this.$confirm('确定收藏该电影, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        item.id = (this.collections.length + 1).toString()
        this.$axios
          .post('/user/add', {
            id: item.id,
            cover: item.cover,
            title: item.title,
            cast: item.cast,
            date: item.date,
            press: item.press,
            summary: item.summary,
            category: item.category}).then(resp => {
            if (resp && resp.status === 200) {
              this.loadCollections()
            }
          })
      }
      ).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消收藏'
        })
      })
    }
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
