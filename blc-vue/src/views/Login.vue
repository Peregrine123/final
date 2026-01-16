<template>
  <div class="bg1">
  <el-form class="login-container" label-position="left"
           label-width="0px">
    <h3>密码登录</h3>
    <p style="color: red;">{{nameHint}}</p>
    <el-form-item>
      <el-input type="text" v-model="loginForm.username"
                auto-complete="off" placeholder="用户名"
                 @blur="checkName"></el-input>
    </el-form-item>
    <el-form-item>
      <el-input type="password" v-model="loginForm.password"
                auto-complete="off" placeholder="密码"
                ></el-input>
    </el-form-item>
    <el-form-item style="width: 100%">
      <el-button type="primary" style="width: 100%;border: none" v-on:click="login">登录</el-button>
    </el-form-item>

    <el-form-item>
      <el-link type="warning" style="float:left" href="/register">没有账号？去注册</el-link>
      <el-link type="primary" style="float:right" href="/">返回</el-link>
    </el-form-item>
  </el-form>
  </div>
</template>
<script>

export default {
  name: 'Login',
  data () {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      nameHint:'',
    }
  },

  methods: {
    checkName(){
      var _this = this
      var url = '/login/check?username='+this.loginForm.username
      this.$axios.get(url)
          .then(resp => {
            // alert(resp.data.code)
            if (resp.data.code === 200) {
              _this.nameHint=''
            } else {
              _this.nameHint=resp.data.message
            }
          })
          .catch(failResponse => {
          })
    },
    login () {
      var _this = this
      console.log(this.$store.state)
      this.$axios
          .post('/login', {
            username: this.loginForm.username,
            password: this.loginForm.password
          })
          .then(resp => {
            if (resp.data.code === 200) {
              _this.$store.commit('login', _this.loginForm.username)
              var path = this.$route.query.redirect
              this.$router.replace({path: path === '/' || path === undefined ? '/index' : path})
            }else {
              this.$alert(resp.data.message, '错误提示', {
                confirmButtonText: '确定'
              })
            }
          })
          .catch(failResponse => {
          })
    }

  }
}
</script>

<style scoped>
.bg1{
  min-height: 100vh;
  width: 100%;
  margin: 0;
  padding: 24px;
  box-sizing: border-box;
  background: url("../assets/bg3.jpg") no-repeat center center;
  background-size: cover;
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  /* margin: 90px auto; removed in favor of flex centering */
  width: 90%; /* Responsive width */
  max-width: 350px; /* Max width */
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}

</style>
