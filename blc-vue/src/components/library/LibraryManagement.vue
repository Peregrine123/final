<template>
  <div v-if="this.role==='admin'">
    <div class="management-header">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item><span style="font-weight: bold;">管理中心</span></el-breadcrumb-item>
        <el-breadcrumb-item>电影管理</el-breadcrumb-item>
      </el-breadcrumb>
      <search-bar ref="searchBar" @onSearch="searchResult" class="header-search"></search-bar>
    </div>

    <edit-form @onSubmit="loadMovies()" ref="edit"></edit-form>

    <el-card class="management-card">
      <el-table
          :data="movies.slice((currentPage-1)*pagesize,currentPage*pagesize)"
          stripe
          style="width: 100%"
          >
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline>
              <el-form-item>
                <span>{{ props.row.summary }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column
            prop="title"
            label="电影名（展开查看简介）"
            fit>
        </el-table-column>
        <el-table-column
            prop="category.name"
            label="分类"
            width="100">
        </el-table-column>
        <el-table-column
            prop="cast"
            label="主演"
            fit>
        </el-table-column>
        <el-table-column
            prop="date"
            label="上映日期"
            width="120">
        </el-table-column>
        <el-table-column
            prop="press"
            label="发行商"
            fit>
        </el-table-column>

        <el-table-column
            fixed="right"
            label="操作"
            width="120">
          <template slot-scope="scope">
            <el-button
                @click.native.prevent="editMovie(scope.row)"
                type="text"
                size="small">
              编辑
            </el-button>
            <el-button
                @click.native.prevent="deleteMovie(scope.row.id)"
                type="text"
                size="small">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin: 20px 0 50px 0;">
        <el-pagination
            layout="total, prev, pager, next, jumper"
            background
            @current-change="handleCurrentChange"
            @current-page="currentPage"
            :page-size="pagesize"
            :total="movies.length">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import EditForm from './EditForm'
import SearchBar from "./SearchBar";
export default {
  name: 'LibraryManagement',
  components: {SearchBar, EditForm},
  data () {
    return {
      movies: [],
      currentPage: 1,
      pagesize: 7,
      role:'',
    }
  },
  mounted () {
    this.loadMovies()
    this.checkRole(JSON.parse(window.localStorage.getItem('user')).username)
  },
  computed: {
    tableHeight () {
      return window.innerHeight - 320
    }
  },
  methods: {
    checkRole(username){
      // alert(username)
      var _this = this
      var url = '/user/role?username='+username
      this.$axios.get(url)
          .then(resp => {
            if (resp.data.code === 200) {
              // console.log(resp.data)
              _this.role=resp.data.result
              // alert(_this.role)
            } else {
              _this.role=''
            }
          })
          .catch(failResponse => {
          })
    },
    deleteMovie (id) {
      this.$confirm('此操作将永久删除该电影资料, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
            this.$axios
                .post('/admin/delete', {id: id}).then(resp => {
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
      // this.$refs.edit.category = {
      //   id: item.category.id.toString()
      // }
    },
    loadMovies () {
      var _this = this
      this.$axios.get('/movies').then(resp => {
        if (resp && resp.status === 200) {
          _this.movies = resp.data
        }
      })
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
    handleCurrentChange: function (currentPage) {
      this.currentPage = currentPage
      // console.log(this.currentPage)
    },
  }
}
</script>

<style scoped>
.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 2%;
  flex-wrap: wrap;
  gap: 15px;
}

.management-card {
  width: 95%;
  max-width: 1400px;
  margin: 18px auto;
}

/* Ensure table expands */
.el-table {
  min-height: 400px;
}
</style>
