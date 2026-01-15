<template>
  <el-menu
      class="categories"
      default-active="0"
      @select="handleSelect"

      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
  >
    <el-menu-item index="0">
      <i class="el-icon-menu"></i>
      <span slot="title">全部</span>
    </el-menu-item>
    <el-menu-item index="1">
      <i class="el-icon-menu"></i>
      <span slot="title">动作</span>
    </el-menu-item>
    <el-menu-item index="2">
      <i class="el-icon-menu"></i>
      <span slot="title">喜剧</span>
    </el-menu-item>
    <el-menu-item index="3">
      <i class="el-icon-menu"></i>
      <span slot="title">剧情</span>
    </el-menu-item>
    <el-menu-item index="4">
      <i class="el-icon-menu"></i>
      <span slot="title">科幻</span>
    </el-menu-item>
    <el-menu-item index="5">
      <i class="el-icon-menu"></i>
      <span slot="title">恐怖</span>
    </el-menu-item>
    <el-menu-item index="6">
      <i class="el-icon-menu"></i>
      <span slot="title">纪录片</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: 'SideMenu',
  data () {
    return {
      cid: ''
    }
  },
  methods: {
    handleSelect (key, keyPath) {
      this.cid = key
      this.$emit('indexSelect')
    }
  }
}
</script>

<style scoped>
.categories {
  position: fixed;
  left: 0;
  top: 59px;
  width: 250px;
  height: 700px;
}
</style>

