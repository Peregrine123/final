<template>
  <div class="articles-area">
    <el-card class="article-card">
      <div>
        <span style="font-size: 20px"><strong>{{article.articleTitle}}</strong></span>
        <el-divider content-position="left">{{article.articleDate}}</el-divider>
        <div class="markdown-body">
          <div v-html="article.articleContentHtml"></div>
        </div>
      </div>
      <div class="article-cover">
        <img :src="article.articleCover" v-if="article.articleCover">
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'ArticleDetails',
    data () {
      return {
        article: []
      }
    },
    mounted () {
      this.loadArticle()
    },
    methods: {
      loadArticle () {
        var _this = this
        this.$axios.get('/article/' + this.$route.query.id).then(resp => {
          if (resp && resp.status === 200) {
            _this.article = resp.data
          }
        })
      }
    }
  }
</script>

<style scoped>
  /*@import "../../styles/markdown.css";*/
  .articles-area {
    width: 95%;
    max-width: 1000px;
    margin: 35px auto 0 auto;
  }
  .article-card {
    text-align: left;
    width: 100%;
  }
  .article-cover img {
    max-width: 100%;
    height: auto;
    margin-top: 20px;
  }
  /* Ensure images in markdown content are also responsive */
  .markdown-body >>> img {
    max-width: 100%;
    height: auto;
  }
</style>
