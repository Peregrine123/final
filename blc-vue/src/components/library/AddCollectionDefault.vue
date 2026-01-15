<template>
  <div>
    <el-row style="height: 800px;">
      <search-bar @onSearch="searchResult" ref="searchBar"></search-bar>
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
        <el-card style="width: 200px;margin-bottom: 40px;height: 300px;float: left;margin-right: 30px" class="collection"
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
    </el-row>
    <el-row>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pagesize"
        :total="movies.length">
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
.cover{
  width:150px;
  height:200px;
  margin-bottom: 7px;
  overflow: hidden;
  cursor: pointer;
  margin-left: 15px;
}
.coverImage{
  width:150px;
  height:200px;
}
.collection{
  border-radius: 25px;
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
