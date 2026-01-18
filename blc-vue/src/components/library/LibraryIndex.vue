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

import MoviesDefault from "@/components/library/MoviesDefault";
import { dedupeMovies } from '@/utils/dedupe'

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
          _this.$refs.moviesArea.movies = dedupeMovies(resp.data)
          _this.$refs.moviesArea.currentPage = 1
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
