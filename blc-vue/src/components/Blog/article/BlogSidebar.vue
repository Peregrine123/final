<template>
  <aside class="blog-sidebar">
    <div class="sidebar-widget">
      <slot name="toc"></slot>
    </div>
    <!-- Future: Author Widget, etc. -->
    <div class="sidebar-widget author-widget">
       <div class="author-card">
          <div class="author-header">
            <el-avatar :size="48" :src="require('@/assets/logo.png')"></el-avatar>
            <div class="author-info">
               <h4>关于作者</h4>
               <p>BLC 技术团队</p>
            </div>
          </div>
          <div class="author-bio">
            <p>专注于电影评论与技术分享的团队。</p>
          </div>
          <div class="author-links">
             <!-- Placeholder links -->
             <i class="el-icon-s-custom"></i>
             <i class="el-icon-share"></i>
          </div>
       </div>
    </div>
  </aside>
</template>

<script>
export default {
  name: 'BlogSidebar'
}
</script>

<style scoped>
.blog-sidebar {
  width: 100%;
}
.sidebar-widget {
  margin-bottom: 32px;
}

.author-card {
  background: #f8fafc;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.author-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.author-info h4 {
  margin: 0 0 4px;
  color: #1e293b;
  font-size: 16px;
}

.author-info p {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.author-bio p {
  margin: 0 0 16px;
  color: #475569;
  font-size: 14px;
  line-height: 1.5;
}

.author-links {
  display: flex;
  gap: 12px;
  color: #94a3b8;
  font-size: 18px;
}
</style>
