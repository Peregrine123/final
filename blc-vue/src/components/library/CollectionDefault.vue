<template>
  <div>
    <div class="collection-grid">
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
      <el-card class="collection-card" bodyStyle="padding:10px" shadow="hover">
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
    </div>
    <el-row>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pagesize"
        :total="collections.length"
        class="pagination">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
import { dedupeMovies } from '@/utils/dedupe'
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
          _this.collections = dedupeMovies(resp.data)
          _this.currentPage = 1
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
  margin: 0 auto 7px auto; /* Center cover */
}
.coverImage{
  width:150px;
  height:200px;
  object-fit: cover;
}
.collection{
  /* border-radius: 25px; removed as it's now on collection-card */
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
