<template>
  <div class="nav-container">
    <div class="nav-header">
      <!-- Left: Brand/Logo -->
      <div class="brand-section" @click="$router.push('/index')">
        <img src="@/assets/logo.png" alt="Logo" class="brand-logo">
        <span class="brand-text">LightTrace</span>
      </div>

      <!-- Center: Navigation -->
      <div class="nav-section">
        <el-menu
            :default-active="currentPath"
            router
            mode="horizontal"
            background-color="transparent"
            text-color="#333"
            active-text-color="#EE9572"
            class="custom-menu">
          <el-menu-item v-for="(item,i) in navList" :key="i" :index="item.name">
            {{ item.navItem }}
          </el-menu-item>
        </el-menu>
      </div>

      <!-- Right: User Actions -->
      <div class="user-section">
        <template v-if="isGuest">
          <el-button type="text" @click="$router.push('/login')" class="auth-btn">登录</el-button>
          <span class="divider">/</span>
          <el-button type="text" @click="$router.push('/register')" class="auth-btn">注册</el-button>
        </template>
        <template v-else>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar icon="el-icon-user-solid" :size="32" class="user-avatar"></el-avatar>
              <span class="username">{{ simpleUsername }}</span>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item v-if="role==='admin'" command="admin-article">影评管理</el-dropdown-item>
              <el-dropdown-item v-if="role==='admin'" command="admin-movie">电影管理</el-dropdown-item>
              <el-dropdown-item v-if="role!==''" command="collection">个人收藏室</el-dropdown-item>
              <el-dropdown-item divided command="logout">注销</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </div>
    </div>
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
      simpleUsername: '', // Pure username without "Welcome..."
      isGuest: true,
      role: '',
    }
  },
  computed: {
    currentPath () {
      return this.$route.path
    }
  },
  created() {
    this.checkLoginStatus()
  },
  methods: {
    checkLoginStatus() {
      const userStr = window.localStorage.getItem('user')
      const username = userStr ? JSON.parse(userStr).username : null

      if (!username) {
        this.isGuest = true
        return
      }

      // Check if session is valid
      this.$axios.get('/login/check?username=' + username)
          .then(resp => {
            if (resp.data.code === 200) {
              this.isGuest = false
              this.simpleUsername = username
              this.checkRole(username)
            } else {
              this.isGuest = true
              this.simpleUsername = ''
            }
          })
          .catch(() => {
            this.isGuest = true
          })
    },
    checkRole(username) {
      this.$axios.get('/user/role?username=' + username)
          .then(resp => {
            if (resp.data.code === 200) {
              this.role = resp.data.result
            } else {
              this.role = ''
            }
          })
          .catch(() => {
            this.role = ''
          })
    },
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/user-profile')
          break
        case 'admin-article':
          this.$router.push('/admin/ArticleManagement')
          break
        case 'admin-movie':
          this.$router.push('/admin/libraryManagement')
          break
        case 'collection':
          this.$router.push('/collectionManagement')
          break
        case 'logout':
          this.logout()
          break
      }
    },
    logout () {
      this.$axios.get('/logout').then(resp => {
        if (resp.data.code === 200) {
          this.$store.commit('logout')
          this.$router.replace('/login')
        }
      }).catch(() => {})
    }
  },
}
</script>

<style scoped>
.nav-container {
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1440px; /* Limit max width for large screens */
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
}

/* Brand Section */
.brand-section {
  display: flex;
  align-items: center;
  cursor: pointer;
  min-width: 150px;
}

.brand-logo {
  height: 40px;
  width: auto;
  margin-right: 10px;
}

.brand-text {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  letter-spacing: 0.5px;
}

/* Nav Section */
.nav-section {
  flex: 1;
  display: flex;
  justify-content: center;
}

.custom-menu.el-menu {
  border-bottom: none !important;
  background: transparent !important;
}

.custom-menu .el-menu-item {
  font-size: 16px;
  font-weight: 500;
  background: transparent !important;
  transition: all 0.3s;
}

.custom-menu .el-menu-item:hover {
  color: #EE9572 !important;
  background-color: rgba(238, 149, 114, 0.1) !important;
}

/* User Section */
.user-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-width: 150px;
}

.auth-btn {
  font-size: 15px;
  color: #666;
  padding: 0 5px;
}

.auth-btn:hover {
  color: #EE9572;
}

.divider {
  color: #ddd;
  margin: 0 5px;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-profile:hover {
  background-color: #f5f5f5;
}

.user-avatar {
  margin-right: 8px;
  background-color: #EE9572;
}

.username {
  font-size: 15px;
  color: #333;
  font-weight: 500;
  margin-right: 4px;
}

</style>
