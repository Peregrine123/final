<template>
  <el-container>
    <el-aside style="width: 200px;margin-top: 20px">
      <SideMenu @indexSelect="listByCategory" ref="sideMenu"></SideMenu>
    </el-aside>
    <el-main>
      <AddCollectionDefault class="collection-area" ref="collectionArea"></AddCollectionDefault>
    </el-main>
  </el-container>
</template>

<script>
import AddCollectionDefault from './AddCollectionDefault'
import SideMenu from './SideMenu'
export default {
  name: 'AddCollectionManagement',
  components: {AddCollectionDefault, SideMenu},
  methods: {
    listByCategory () {
      var _this = this
      var cid = this.$refs.sideMenu.cid
      var url = 'categories/' + cid + '/movies'
      this.$axios.get(url).then(resp => {
        if (resp && resp.status === 200) {
          _this.$refs.collectionArea.movies = resp.data
        }
      })
    }
  }
}
</script>

<style scoped>
.collection-area{
  width: 95%;
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
}
</style>
