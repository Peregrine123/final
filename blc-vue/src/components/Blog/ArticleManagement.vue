<template>
  <div v-if="this.role==='admin'">
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item><span style="font-weight:bold;">管理中心</span></el-breadcrumb-item>
        <el-breadcrumb-item>影评管理</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-link href="/admin/editor" :underline="false" target="_blank" class="add-link">
      <el-button type="success">撰写影评</el-button>
    </el-link>
    <el-card style="margin: 18px 2%;width: 95%">
      <el-table
          :data="articles"
          stripe
          style="width: 100%"
          :max-height="tableHeight">
        <el-table-column
            type="selection"
            width="55">
        </el-table-column>
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline>
              <el-form-item>
                <span>{{ props.row.articleAbstract }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column
            prop="articleTitle"
            label="题目（展开查看摘要）"
            fit>
        </el-table-column>
        <el-table-column
            prop="articleDate"
            label="发布日期"
            width="200">
        </el-table-column>
        <el-table-column
            fixed="right"
            label="操作"
            width="180">
          <template slot-scope="scope">
            <el-button
                @click.native.prevent="viewArticle(scope.row.id)"
                type="text"
                size="small">
              查看
            </el-button>
            <el-button
                @click.native.prevent="editArticle(scope.row)"
                type="text"
                size="small">
              编辑
            </el-button>
            <el-button
                @click.native.prevent="deleteArticle(scope.row.id)"
                type="text"
                size="small">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 20px 0 50px 0">
        <el-pagination
            background
            style="float:right;"
            layout="total, prev, pager, next, jumper"
            @current-change="handleCurrentChange"
            :page-size="pageSize"
            :total="total">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ArticleManagement',
  data () {
    return {
      articles: [],
      pageSize: 10,
      total: 0,
      role:'',
    }
  },
  mounted () {
    this.loadArticles()
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
    loadArticles () {
      var _this = this
      this.$axios.get('/article/' + this.pageSize + '/1').then(resp => {
        if (resp && resp.status === 200) {
          _this.articles = resp.data.content
          _this.total = Number(resp.data.totalElements)//string=>number
        }
      })
    },
    handleCurrentChange (page) {
      var _this = this
      this.$axios.get('/article/' + this.pageSize + '/' + page).then(resp => {
        if (resp && resp.status === 200) {
          _this.articles = resp.data.content
          _this.total = Number(resp.data.totalElements)
        }
      })
    },
    viewArticle (id) {
      let articleUrl = this.$router.resolve(
          {
            path: '/blog/article',
            query: {
              id: id
            }
          }
      )
      window.open(articleUrl.href, '_blank')
    },
    editArticle (article) {
      this.$router.push(
          {
            name: 'ArticleEditor',
            params: {
              article: article
            }
          }
      )
    },
    deleteArticle (id) {
      this.$confirm('此操作将永久删除该文章, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
            this.$axios
                .delete('/admin/article/' + id).then(resp => {
              if (resp && resp.status === 200) {
                this.loadArticles()
              }
            })
          }
      ).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    }
  }
}
</script>

<style scoped>
.add-link {
  margin: 18px 0 15px 10px;
  float: left;
}
</style>

