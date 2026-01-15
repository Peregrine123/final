<template>
  <div>
    <el-row style="height: 840px;">

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
          <div class="cover" @click="editMovie(item)">
            <img :src="item.cover" alt="封面">
          </div>
          <div class="info">
            <div class="title">
              <a href="">{{item.title}}</a>
            </div>
            <i class="el-icon-delete" @click="deleteMovie(item.id)"></i>
          </div>
          <div class="author">{{item.cast}}</div>
        </el-card>
      </el-tooltip>
      <edit-form @onSubmit="loadMovies()" ref="edit"></edit-form>
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
  name: 'Movies',
  components: {EditForm, SearchBar},
  data () {
    return {
      movies: [],
      currentPage: 1,
      pagesize: 17
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
    deleteMovie (id) {
      this.$confirm('此操作将永久删除该电影, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
            this.$axios
                .post('/delete', {id: id}).then(resp => {
                  //post 不能像 get 请求那样直接把参数写在 url 里，而需要以键值对的方式传递。
              if (resp && resp.status === 200) {
                this.loadMovies()
              }
            })
          }
      ).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
      // alert(id)
    },
    editMovie (item) {
      this.$refs.edit.dialogFormVisible = true
      this.$refs.edit.dialogTitle="修改电影"
      this.$refs.edit.form = {
        id: item.id,
        cover: item.cover,
        title: item.title,
        cast: item.cast,
        date: item.date,
        press: item.press,
        summary: item.summary,
        category: {
          id: item.category.id.toString(),
          name: item.category.name
        }
      }
    }
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

.el-icon-delete {
  cursor: pointer;
  float: right;
}

.switch {
  display: flex;
  position: absolute;
  left: 780px;
  top: 25px;
}

a {
  text-decoration: none;
}

a:link, a:visited, a:focus {
  color: #3377aa;
}

</style>

