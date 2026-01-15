<template>
  <div>
    <el-menu
        :default-active="currentPath"
        router
        mode="horizontal"
        background-color="#EEE9E9"
        text-color="black"
        active-text-color="#EE9572"
        style="min-width: 1300px">
      <el-menu-item v-for="(item,i) in navList" :key="i" :index="item.name">
        {{ item.navItem }}
      </el-menu-item>

      <el-submenu style="float:right;margin-right: 10px;">
        <template slot="title">管理中心</template>
        <el-menu-item  onclick="javascript:window.open('/user-profile')">个人资料</el-menu-item>
        <el-menu-item  v-if="this.role==='admin'" onclick="javascript:window.open('/admin/ArticleManagement')">影评管理</el-menu-item>
        <el-menu-item  v-if="this.role==='admin'" onclick="javascript:window.open('/admin/libraryManagement')">电影管理</el-menu-item>
        <el-menu-item v-if="this.role!==''" onclick="javascript:window.open('/collectionManagement')" >个人收藏室</el-menu-item>
        <el-menu-item  v-on:click="logout">注销</el-menu-item>
      </el-submenu>

      <span style="position: absolute;padding-top: 20px;right: 40%;font-size: 20px;font-weight: bold">FilmTalk影博汇——提供专业影评和多样影单</span>
      <span style="position: absolute;padding-top: 20px;right: 13%;font-size: 16px;font-weight: bold">{{welcome}}</span>

    </el-menu>
  </div>
</template>

<script>
export default {
  name: 'NavMenu',
  data () {
    return {
      navList: [
        {name: '/index', navItem: '首页'},
        {name: '/blog', navItem: '博客'},
        {name: '/library', navItem: '影单'},
      ],
      keywords: '',
      welcome:'游客访问',
      role:'',
    }
  },
  computed: {
    currentPath () {
      return this.$route.path

    }
  },
  created() {
    this.checkName()
    if(this.welcome!='游客访问'){
      this.checkRole(JSON.parse(window.localStorage.getItem('user')).username)
    }
  },
  methods: {
    checkName(){
      this.welcome=window.localStorage.getItem('user') == null ? '' : JSON.parse(window.localStorage.getItem('user')).username
      var _this = this
      var url = '/login/check?username='+_this.welcome
      this.$axios.get(url)
          .then(resp => {
            if (resp.data.code === 200) {
              _this.welcome='欢迎您：'+this.welcome
            } else {
              _this.welcome='游客访问'
            }
          })
          .catch(failResponse => {
          })
    },
    checkRole(username){
      // alert(username)
      var _this = this
      var url = '/user/role?username='+username
      this.$axios.get(url)
          .then(resp => {
            if (resp.data.code === 200) {
              // console.log(resp.data)
              _this.role=resp.data.result
              // alert(_this.role)
            } else {
              _this.role=''
            }
          })
          .catch(failResponse => {
          })
    },
    handleSelect (key, keyPath) {
      console.log(key, keyPath)
    },
    logout () {
      var _this = this
      this.$axios.get('/logout').then(resp => {
        if (resp.data.code === 200) {
          _this.$store.commit('logout')
          _this.$router.replace('/login')
        }
      }).catch(failResponse => {})
    }
  },
}
</script>

<style scoped>
a{
  text-decoration: none;
}


.el-icon-switch-button {
  cursor: pointer;
  outline:0;/*取消按table键切换时出现的默认自带的很丑的框*/
}

</style>
