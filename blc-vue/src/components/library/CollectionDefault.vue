<template>
  <div>
    <el-row style="height: 800px;">
    <el-tooltip effect="dark" placement="right"
                v-for="item in collections.slice((currentPage-1)*pagesize,currentPage*pagesize)"
                :key="item.id">
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
          <img :src="image" alt="选择" @click="cancelCollection(item.id)" class="collectionImage">
        </div>
<!--        <div class="cast">{{item.cast}}</div>-->
      </el-card>
    </el-tooltip>
    </el-row>
    <el-row>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pagesize"
        :total="collections.length">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'CollectionDefault',
  data () {
    return {
      collections: [],
      currentPage: 1,
      pagesize: 8,
      image: '/static/collection.png'
    }
  },
  mounted: function () {
    this.loadCollections()
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
    handleCurrentChange: function (currentPage) {
      this.currentPage = currentPage
    },
    cancelCollection (id) {
      console.log(id)
      this.$confirm('此操作将永久删除该收藏, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios
          .post('/user/delete', {id: id}).then(resp => {
            if (resp && resp.status === 200) {
              this.loadCollections()
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
