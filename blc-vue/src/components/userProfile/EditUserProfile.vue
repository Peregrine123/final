<template>
  <div class="edit-profile-container">
    <h1>编辑个人资料</h1>
    <form @submit.prevent="saveProfile" class="edit-profile-form">
      <div class="form-group">
        <label>用户名</label>
        <input type="text" v-model="userProfile.username" placeholder="用户名" />
      </div>
      <div class="form-group">
        <label>名字</label>
        <input type="text" v-model="userProfile.firstName" placeholder="名字" />
      </div>
      <div class="form-group">
        <label>姓氏</label>
        <input type="text" v-model="userProfile.lastName" placeholder="姓氏" />
      </div>
      <div class="form-group">
        <label>电子邮件</label>
        <input type="email" v-model="userProfile.email" placeholder="电子邮件" />
      </div>
      <div class="form-group">
        <label>电话</label>
        <input type="text" v-model="userProfile.phone" placeholder="电话" />
      </div>
      <div class="form-group">
        <label>国家</label>
        <input type="text" v-model="userProfile.country" placeholder="国家" />
      </div>
      <div class="form-group">
        <label>州/省</label>
        <input type="text" v-model="userProfile.state" placeholder="州/省" />
      </div>
      <div class="form-group">
        <label>城市</label>
        <input type="text" v-model="userProfile.city" placeholder="城市" />
      </div>
      <div class="form-group">
        <label>地址</label>
        <input type="text" v-model="userProfile.address" placeholder="地址" />
      </div>
      <div class="form-group">
        <label>简介</label>
        <textarea v-model="userProfile.bio" placeholder="简介"></textarea>
      </div>
      <div class="form-group">
        <label>新密码</label>
        <input type="password" v-model="newPassword" placeholder="新密码" />
      </div>
      <div class="form-group">
        <label>确认新密码</label>
        <input type="password" v-model="confirmPassword" placeholder="确认新密码" />
      </div>
      <div class="form-buttons">
        <button type="submit" class="save-button">保存</button>
        <button type="button" class="cancel-button" @click="cancelEdit">取消</button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userProfile: {
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
      newPassword: '',
      confirmPassword: ''
    };
  },
  created() {
    this.fetchUserProfile();
  },
  methods: {
    fetchUserProfile() {
      const userId = this.$route.params.id;
      this.$axios.get(`/users/${userId}`).then(response => {
        this.userProfile = response.data;
      }).catch(error => {
        console.error("获取用户资料失败: ", error);
      });
    },
    saveProfile() {
      if (this.newPassword !== this.confirmPassword) {
        this.$alert('新密码和确认新密码不匹配', '错误提示', {
          confirmButtonText: '确定'
        });
        return;
      }

      // 包含新密码字段的userProfile
      const updatedUserProfile = {
        ...this.userProfile,
        password: this.newPassword ? this.newPassword : this.userProfile.password
      };

      if (this.userProfile.id) {
        // 更新现有的用户资料
        this.$axios.put(`/users/${this.userProfile.id}`, updatedUserProfile).then(() => {
          this.showSuccessAlert();
        }).catch(error => {
          console.error("保存用户资料失败: ", error);
        });
      } else {
        // 创建新的用户资料
        this.$axios.post('/users', updatedUserProfile).then(() => {
          this.showSuccessAlert();
        }).catch(error => {
          console.error("创建用户资料失败: ", error);
        });
      }
    },
    showSuccessAlert() {
      this.$alert('保存成功', '成功提示', {
        confirmButtonText: '确定',
        callback: () => {
          this.$router.push('/user-profile'); // 保存成功后跳转到个人资料页面
        }
      });
    },
    cancelEdit() {
      this.$router.push('/user-profile'); // 点击取消按钮后跳转到个人资料页面
    }
  }
};
</script>

<style scoped>
.edit-profile-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  font-family: 'Arial', sans-serif;
  text-align: center;
}

/* 标题样式 */
.edit-profile-container h1 {
  font-size: 2em;
  margin-bottom: 20px;
  color: #333;
}

/* 表单样式 */
.edit-profile-form {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.form-group {
  width: 100%;
  margin-bottom: 15px;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s ease-in-out;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #007bff;
  outline: none;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

/* 按钮容器样式 */
.form-buttons {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

/* 按钮样式 */
.save-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease-in-out, transform 0.3s ease-in-out;
}

.save-button:hover {
  background-color: #0056b3;
  transform: translateY(-3px);
}

/* 取消按钮样式 */
.cancel-button {
  padding: 10px 20px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin-left: 10px; /* 墑 margin-left 确保两个按钮有 间 */
  transition: background-color 0.3s ease-in-out, transform 0.3s ease-in-out;
}

.cancel-button:hover {
  background-color: #5a6268;
  transform: translateY(-3px);
}
</style>
