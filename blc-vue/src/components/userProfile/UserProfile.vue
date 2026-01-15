<template>
  <div class="profile-container">
    <h1>个人资料</h1>
    <div v-if="userProfile" class="profile-card">
      <div class="profile-info">
        <div class="info-item">
          <strong>用户名：</strong>
          <span :class="{ 'unset-value': !userProfile.username }">{{ userProfile.username || "暂未设置" }}</span>
        </div>
        <div class="info-item">
          <strong>名字：</strong>
          <span :class="{ 'unset-value': !(userProfile.firstName && userProfile.lastName) }">{{ (userProfile.firstName && userProfile.lastName) ? (userProfile.firstName + ' ' + userProfile.lastName) : "暂未设置" }}</span>
        </div>
        <div class="info-item">
          <strong>电子邮件：</strong>
          <span :class="{ 'unset-value': !userProfile.email }">{{ userProfile.email || "暂未设置" }}</span>
        </div>
        <div class="info-item">
          <strong>电话：</strong>
          <span :class="{ 'unset-value': !userProfile.phone }">{{ userProfile.phone || "暂未设置" }}</span>
        </div>
        <div class="info-item">
          <strong>地址：</strong>
          <span :class="{ 'unset-value': !userProfile.country && !userProfile.state && !userProfile.city && !userProfile.address }">{{ formatAddress(userProfile) }}</span>
        </div>
        <div class="info-item">
          <strong>简介：</strong>
          <span :class="{ 'unset-value': !userProfile.bio }">{{ userProfile.bio || "暂未设置" }}</span>
        </div>
        <button @click="goToEditPage" class="edit-button center-button">编辑</button>
      </div>
    </div>
    <div v-else class="loading">
      <p>加载中...</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userProfile: null,
      showPassword: false
    };
  },
  created() {
    this.fetchUserProfile();
  },
  methods: {
    fetchUserProfile() {
      const username = JSON.parse(window.localStorage.getItem('user')).username; // 获取当前用户名
      console.log("当前用户名: " + username);
      if (username) {
        console.log("进入获取id: " + username);
        var url = '/users/current?username=' + username;
        console.log("url是: " + url);
        this.$axios.get(url).then(response => {
          this.userProfile = response.data;
        }).catch(error => {
          console.log("获取当前用户信息失败");
          console.error("获取当前用户信息失败: ", error); // 调试信息
        });
      } else {
        console.log("当前用户名不存在");
        console.error("当前用户名不存在");
      }
    },
    goToEditPage() {
      this.$router.push(`/edit-user-profile/${this.userProfile.id}`);
    },
    formatAddress(profile) {
      // 检查地址字段是否全部为空
      if (!profile.country && !profile.state && !profile.city && !profile.address) {
        return "暂未设置";
      }
      // 拼接地址信息
      return `${profile.country || ""} ${profile.state || ""} ${profile.city || ""} ${profile.address || ""}`.trim();
    },
  }
};
</script>

<style scoped>
.profile-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.profile-card {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.profile-info {
  text-align: left;
  width: 100%; /* 使 profile-info 在容器中占据100%的宽度 */
}

.info-item {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid #eaeaea; /* 添加底部边框 */
  padding: 10px 0; /* 增加内边距 */
}

.profile-info p {
  font-size: 16px;
  line-height: 1.5;
  margin: 10px 0;
}

.profile-info strong {
  color: #333;
}

.unset-value {
  color: #888; /* 灰色 */
}

.toggle-password {
  margin-left: 10px;
  padding: 5px 10px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.toggle-password:hover {
  background-color: #5a6268;
}

.edit-button {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.edit-button:hover {
  background-color: #0056b3;
}

.loading {
  font-size: 18px;
  color: #888;
}

.center-button {
  display: block;
  margin: 20px auto; /* 调整按钮居中 */
}
</style>
