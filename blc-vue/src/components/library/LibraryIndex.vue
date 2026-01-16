<template>
  <el-container>
    <el-aside style="width: 200px;">
      <SideMenu @indexSelect="listByCategory" ref="sideMenu"></SideMenu>
    </el-aside>
    <el-main>
      <MoviesDefault class="movies-area" ref="moviesArea"></MoviesDefault>
    </el-main>
  </el-container>
</template>

<script>
import SideMenu from './SideMenu'
import Movies from './Movies'

import MoviesDefault from "@/components/library/MoviesDefault";

export default {
  name: 'AppLibrary',
  components: {MoviesDefault, SideMenu},
  methods: {
    listByCategory () {
      var _this = this
      var cid = this.$refs.sideMenu.cid
      var url = 'categories/' + cid + '/movies'
      this.$axios.get(url).then(resp => {
        if (resp && resp.status === 200) {
          _this.$refs.moviesArea.movies = resp.data
        }
      })
    }
  }
}
</script>

<style scoped>
.movies-area {
  width: 100%;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
</style>


