<template>
  <div style="margin-bottom: 30px;display: flex;justify-content: center;align-items: center">
    <el-input
        @keyup.enter.native="searchClick"
        placeholder="通过电影名或主演搜索..."
        prefix-icon="el-icon-search"
        size="small"
        style="width: 400px;margin-right: 10px"
        v-model="keywords">
    </el-input><!--onchange不好用，效果和onblur一样，这里用watch监听keywords变化-->
  </div>
</template>

<script>
export default {
  name: 'SearchBar',
  data () {
    return {
      keywords: '',
      movies: [],
      cardLoading: []
    }
  },
  methods: {
    searchClick () {
      this.$emit('onSearch')
    }
  },
  watch: {
    keywords: {
      handler() {
        this.$emit('onSearch')
      }
    }
  },
}
</script>

<style scoped>

</style>

