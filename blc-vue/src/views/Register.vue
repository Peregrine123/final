<template>
  <div class="bg2">
  <el-form class="login-container" label-position="left" label-width="0px" v-loading="loading">
    <h3>用户注册</h3>
    <p style="color: red;">{{nameHint}}</p>
    <el-form-item>
      <el-input type="text" v-model="loginForm.username" auto-complete="off" placeholder="用户名" @blur="checkName"></el-input>
    </el-form-item>
    <el-form-item>
      <el-input type="password" v-model="loginForm.password" auto-complete="off" placeholder="密码" show-password></el-input>
    </el-form-item>
    <el-form-item style="width: 100%">
      <el-button type="primary" style="width: 100%;border: none" v-on:click="register">注册</el-button>
    </el-form-item>
    <el-form-item>
      <el-link type="warning" style="float:left" href="/login">已有账户？去登录</el-link>
      <el-link type="primary" style="float:right" href="/">返回</el-link>
    </el-form-item>
  </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
        city: '',
        state: '',
        country: '',
        bio: '',
        profilePicture: ''
      },
      loading: false,
      nameHint: '',
    }
  },
  methods: {
    checkName() {
      var _this = this;
      var url = '/register/check?username=' + this.loginForm.username;
      this.$axios.get(url)
          .then(resp => {
            if (resp.data.code === 200) {
              _this.nameHint = ''
            } else {
              _this.nameHint = resp.data.message
            }
          })
          .catch(failResponse => {
          })
    },
    register() {
      var _this = this
      this.$axios
          .post('/register', {
            username: this.loginForm.username,
            password: this.loginForm.password
          })
          .then(resp => {
            // alert(resp.data.code)
            if (resp.data.code === 200) {
              this.$alert('注册成功', '成功提示', {
                confirmButtonText: '确定'
              })
              _this.$router.replace('/login')
            } else {
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
.bg2 {
  min-height: 100vh;
  width: 100%;
  margin: 0;
  padding: 24px 0;
  box-sizing: border-box;
  background: url("../assets/bg2.jpg") no-repeat center center;
  background-size: cover;
}
.login-container {
  border-radius: 15px;
  background-clip: padding-box;
  margin: 90px auto;
  width: 90%; /* Responsive */
  max-width: 350px; /* Fixed max width */
  padding: 35px 35px 15px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}
</style>
