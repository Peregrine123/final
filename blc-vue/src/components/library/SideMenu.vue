<template>
  <el-menu
      class="categories"
      default-active="0"
      @select="handleSelect"

      background-color="#ffffff"
      text-color="#303133"
      active-text-color="#EE9572"
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
  position: sticky;
  top: 0;
  width: 100%;
  height: 100vh;
  padding: 20px 0;
  border-right: 1px solid #ebeef5;
  border-radius: 0;
  background: #f9fafc;
  box-shadow: none;
  overflow-y: auto;
}

/* Element-UI internals: use deep selector to style menu items under scoped CSS. */
.categories /deep/ .el-menu-item {
  height: 50px;
  line-height: 50px;
  margin: 0;
  border-radius: 0;
  padding-left: 30px !important; /* Adjust padding for alignment */
}

.categories /deep/ .el-menu-item:hover {
  background-color: #ecf5ff !important;
}

.categories /deep/ .el-menu-item.is-active {
  background-color: #ecf5ff !important;
  color: #409EFF; /* Use a standard primary color or keep the orange if preferred, sticking to design doc suggestions */
  border-right: 3px solid #409EFF;
}

.categories /deep/ .el-menu-item i {
  color: #909399;
  margin-right: 10px;
}

.categories /deep/ .el-menu-item.is-active i {
  color: #409EFF;
}
</style>
