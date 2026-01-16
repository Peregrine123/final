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
    <el-menu-item index="7">
      <i class="el-icon-menu"></i>
      <span slot="title" onclick="javascript:window.open('/addCollectionManagement')">添加</span>
    </el-menu-item>
  </el-menu>
</template>

<script>
export default {
  name: 'WorkMenu',
  data () {
    return {
      cid: ''
    }
  },
  methods: {
    handleSelect (key, keyPath) {
      this.cid = key
      // el-menu index is a string; "添加" (index=7) should not trigger listByCategory.
      if (this.cid !== '7') {
        this.$emit('indexSelect')
      }
    }
  }
}
</script>

<style scoped>
.categories {
  position: sticky;
  top: 80px;
  width: 100%;
  padding: 10px;
  border-right: 0;
  border: 1px solid #ebeef5;
  border-radius: 14px;
  background: #fff;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  max-height: calc(100vh - 140px);
  overflow: auto;
}

/* Element-UI internals: use deep selector to style menu items under scoped CSS. */
.categories /deep/ .el-menu-item {
  height: 44px;
  line-height: 44px;
  border-radius: 10px;
  margin: 4px 0;
}

.categories /deep/ .el-menu-item:hover {
  background-color: rgba(238, 149, 114, 0.10) !important;
}

.categories /deep/ .el-menu-item.is-active {
  background-color: rgba(238, 149, 114, 0.16) !important;
  font-weight: 600;
}

.categories /deep/ .el-menu-item i {
  color: #909399;
}

.categories /deep/ .el-menu-item.is-active i {
  color: #EE9572;
}
</style>
