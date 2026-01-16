
<template>
  <div class="articles-area">
    <div class="cards-list">
      <el-card :body-style="{ padding: '0px' }" v-for="article in articles" :key="article.id" class="blog-card" shadow="hover">

        <div class="meta">
          <!-- 设置文章封面图片为背景图 -->
          <div class="photo" :style="{ backgroundImage: 'url(' + article.articleCover + ')' }"></div>
        </div>
        <div class="description">
          <h1>{{ article.articleTitle }}</h1>
          <h2>{{ article.articleDate }}</h2>
          <p>{{ article.articleAbstract }}</p>
          <p class="read-more">
            <router-link :to="{ path: 'blog/article', query: { id: article.id } }">Read More</router-link>
          </p>
        </div>
      </el-card>
    </div>
    <el-pagination
        background
        layout="total, prev, pager, next, jumper"
        @current-change="handleCurrentChange"
        :page-size="pageSize"
        :total="total"
        class="pagination">
    </el-pagination>
  </div>
</template>

<script>

export default {
  name: 'Articles',
  data () {
    return {
      articles: [],
      pageSize: 4,
      total: 0
    }
  },
  mounted () {
    this.loadArticles()
  },
  methods: {
    loadArticles () {
      var _this = this
      this.$axios.get('/article/' + this.pageSize + '/1').then(resp => {
        if (resp && resp.status === 200) {
          _this.articles = resp.data.content
          _this.total = Number(resp.data.totalElements)
        }
      })
    },
    handleCurrentChange (page) {
      var _this = this
      this.$axios.get('/article/' + this.pageSize + '/' + page).then(resp => {
        if (resp && resp.status === 200) {
          _this.articles = resp.data.content
          _this.total = Number(resp.data.totalElements)
        }
      })
    }
  }
}
</script>

<style scoped>
.articles-area {
  width: 95%;
  max-width: 1200px;
  margin: 0 auto;
  /* Removed fixed height to allow content to grow */
  padding-bottom: 40px;
}

/* ElementUI's internal DOM isn't affected by scoped selectors; use deep selector here. */
.blog-card >>> .el-card__body {
  display: flex;
  flex-direction: column; /* mobile-first */
  width: 100%;
  padding: 0 !important;
}

.blog-card {
  width: 100%;
  margin: 1rem auto;
  box-shadow: 0 3px 7px -1px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  background: #fff;
  line-height: 1.4;
  font-family: sans-serif;
  border-radius: 5px;
  overflow: hidden;
  z-index: 0;
}
.blog-card a {
  color: inherit;
}
.blog-card a:hover {
  color: #3b70fc;
}
.blog-card:hover .photo {
  transform: scale(1.3) rotate(3deg);
}
.blog-card .meta {
  position: relative;
  width: 100%; /* Mobile full width */
  z-index: 0;
  height: 200px;
}
.blog-card .photo {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  transition: transform 0.2s;
}
.blog-card .details,
.blog-card .details ul {
  margin: auto;
  padding: 0;
  list-style: none;
}
.blog-card .details {
  position: absolute;
  top: 0;
  bottom: 0;
  left: -100%;
  margin: auto;
  transition: left 0.2s;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 10px;
  width: 100%;
  font-size: 0.9rem;
}
.blog-card .details a {
  -webkit-text-decoration: dotted underline;
  text-decoration: dotted underline;
}
.blog-card .details ul li {
  display: inline-block;
}
.blog-card .details .author:before {
  font-family: FontAwesome;
  margin-right: 10px;
  content: "";
}
.blog-card .details .date:before {
  font-family: FontAwesome;
  margin-right: 10px;
  content: "";
}
.blog-card .details .tags ul:before {
  font-family: FontAwesome;
  content: "";
  margin-right: 10px;
}
.blog-card .details .tags li {
  margin-right: 2px;
}
.blog-card .details .tags li:first-child {
  margin-left: -4px;
}
.blog-card .description {
  /* height: 150px; removed fixed height to avoid overflow */
  min-height: 150px;
  padding: 1rem;
  width: 100%;
  background: #fff;
  position: relative;
  z-index: 1;
  box-sizing: border-box;
}
.blog-card .description h1,
.blog-card .description h2 {
  font-family: Poppins, sans-serif;
}
.blog-card .description h1 {
  line-height: 1;
  margin: 0;
  font-size: 1.7rem;
}
.blog-card .description h2 {
  font-size: 1rem;
  font-weight: 300;
  text-transform: uppercase;
  color: #a2a2a2;
  margin-top: 5px;
}
.blog-card .description .read-more {
  text-align: right;
}
.blog-card .description .read-more a {
  color: #3b70fc;
  display: inline-block;
  position: relative;
}
.blog-card .description .read-more a:after {
  content: "✨";
  font-family: FontAwesome;
  margin-left: -10px;
  opacity: 0;
  vertical-align: middle;
  transition: margin 0.3s, opacity 0.3s;
}
.blog-card .description .read-more a:hover:after {
  margin-left: 5px;
  opacity: 1;
}
.blog-card p {
  position: relative;
  margin: 1rem 0 0;
}
.blog-card p:first-of-type {
  margin-top: 1.25rem;
}
.blog-card p:first-of-type:before {
  content: "";
  position: absolute;
  height: 5px;
  background: #3b70fc;
  width: 35px;
  top: -0.75rem;
  border-radius: 3px;
}
.blog-card:hover .details {
  left: 0%;
}
@media (min-width: 640px) {
  .blog-card >>> .el-card__body {
    flex-direction: row;
  }
  .blog-card .meta {
    flex-basis: 40%;
    height: auto; /* Let it fill height */
    min-height: 200px;
  }
  .blog-card .description {
    flex-basis: 60%;
  }
  .blog-card .description:before {
    transform: skewX(-3deg);
    content: "";
    background: #fff;
    width: 30px;
    position: absolute;
    left: -10px;
    top: 0;
    bottom: 0;
    z-index: -1;
  }
  .blog-card.alt {
    flex-direction: row-reverse;
  }
  .blog-card.alt .description:before {
    left: inherit;
    right: -10px;
    transform: skew(3deg);
  }
  .blog-card.alt .details {
    padding-left: 25px;
  }
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
