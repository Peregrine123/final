<template>
<el-container>
<el-aside style="width: 200px;margin-top: 20px;border-top-right-radius: 20px;border-bottom-right-radius: 20px">
  <WorkMenu @indexSelect="listByCategory" ref="sideMenu"></WorkMenu>
</el-aside>
<el-container>
<el-header style="height: auto; padding-bottom: 20px;">
  <h2>最近收藏</h2>
  <SlidShow class="movie-show" ref="movieShow"></SlidShow>
</el-header>
<el-main>
  <h2>收藏历史</h2>
  <CollectionDefault class="collection-default" ref="collectionDefault"></CollectionDefault>
</el-main>
</el-container>
</el-container>
</template>

<script>
import SlidShow from './SlidShow'
import CollectionDefault from './CollectionDefault'
import WorkMenu from './WorkMenu'
export default {
  name: 'CollectionManagement',
  components: {WorkMenu, CollectionDefault, SlidShow},
  methods: {
    listByCategory () {
      var _this = this
      var cid = this.$refs.sideMenu.cid
      var url = 'categories/' + cid + '/collections'
      this.$axios.get(url).then(resp => {
        if (resp && resp.status === 200) {
          _this.$refs.collectionDefault.collections = resp.data
        }
      })
    }
  }
}
</script>

<style scoped>
.movie-show{
  width: 95%;
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
}
.collection-default{
  width: 95%;
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  align-content: center;
}

</style>
