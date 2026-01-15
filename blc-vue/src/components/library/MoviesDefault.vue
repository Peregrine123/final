<template>
  <div>
    <el-row style="height: 620px;">
      <search-bar @onSearch="searchResult" ref="searchBar"></search-bar>
      <el-tooltip effect="dark" placement="right"
                  v-for="item in movies.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                  :key="item.id">
        <p slot="content" style="font-size: 14px;margin-bottom: 6px;">{{item.title}}</p>
        <p slot="content" style="font-size: 13px;margin-bottom: 6px">
          <span>{{item.cast}}</span> /
          <span>{{item.date}}</span> /
          <span>{{item.press}}</span>
        </p>
        <p slot="content" style="width: 300px" class="abstract">{{item.summary}}</p>
        <el-card style="width: 135px;margin-bottom: 20px;height: 233px;float: left;margin-right: 15px" class="movie"
                 bodyStyle="padding:10px" shadow="hover">
          <div class="cover">
            <img :src="item.cover" alt="封面">
          </div>
          <div class="info">
            <div class="title">
              <a href="" @click.prevent="goToMovieDetails(item.id)">{{item.title}}</a>
<!--              yiiiiiiiiiiiiiiiiiiiiiiiii-->
            </div>
          </div>
          <div class="cast">{{item.cast}}</div>
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
import EditForm from "./EditForm";
import SearchBar from './SearchBar'
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
          _this.movies = resp.data
        }
      })
    },
    handleCurrentChange: function (currentPage) {
      this.currentPage = currentPage
      // console.log(this.currentPage)
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
    goToMovieDetails(id) {
      this.$router.push({ name: 'MovieDetails', params: { id } });
    },
  }
}
</script>
<style scoped>

.cover {
  width: 115px;
  height: 172px;
  margin-bottom: 7px;
  overflow: hidden;
  cursor: pointer;
}

img {
  width: 115px;
  height: 172px;
  /*margin: 0 auto;*/
}

.title {
  font-size: 14px;
  text-align: left;
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

