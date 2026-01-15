<template>
  <div>
    <el-row style="margin: 18px 0px 0px 18px ">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item><span style="font-weight: bold;">管理中心</span></el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/admin/ArticleManagement'}">影评管理</el-breadcrumb-item>
        <el-breadcrumb-item>编辑器</el-breadcrumb-item>
      </el-breadcrumb>
    </el-row>
    <el-row>
      <el-input
          v-model="article.articleTitle"
          style="margin: 10px 0px;font-size: 18px;"
          placeholder="请输入标题"></el-input>
    </el-row>
    <el-row style="height: calc(100vh - 140px);">
      <mavon-editor
          v-model="article.articleContentMd"
          style="height: 100%;"
          ref=md
          @save="saveArticles"
          fontSize="16px">
        <button type="button" class="op-icon el-icon-document" :title="'摘要/封面'" slot="left-toolbar-after"
                @click="dialogVisible = true"></button>
      </mavon-editor>
      <el-dialog
          :modal-append-to-body="false"
          :visible.sync="dialogVisible"
          width="35%">
        <el-divider content-position="left">摘要</el-divider>
        <el-input
            type="textarea"
            v-model="article.articleAbstract"
            rows="6"
            maxlength="255"
            show-word-limit></el-input>
        <el-divider content-position="left">封面</el-divider>
        <div style="margin-top: 20px;text-align: left;">
          <el-input v-model="article.articleCover" autocomplete="off" placeholder="图片 URL"></el-input>
          <img-upload @onUpload="uploadImg" ref="imgUpload" style="margin-top: 5px"></img-upload>
          <el-image style="width: 100px; height: 100px" :src="article.articleCover">
            <div slot="error" class="image-slot"><!--加载失败-->
            </div>
          </el-image>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </span>
      </el-dialog>
    </el-row>
  </div>
</template>

<script>
import ImgUpload from "../content/ImgUpload";

export default {
  name: 'ArticleEditor',
  components: {ImgUpload},
  data () {
    return {
      article: {
        id: '',
        articleTitle: '',
/*        articleContentMd: value,
        articleContentHtml: render,*/
        articleAbstract: '',
        articleCover: '',
        articleDate: ''
      },
      dialogVisible: false
    }
  },
  mounted () {//检测是否编辑
    if (this.$route.params.article) {
      this.article = this.$route.params.article
    }
  },
  methods: {
    getTime(){
      var date = new Date();
      var year = date.getFullYear(); //月份从0~11，所以加一
      var dateArr = [date.getMonth() + 1,date.getDate(),date.getHours(),date.getMinutes(),date.getSeconds()];
      for(var i=0;i<dateArr.length;i++){//5项
        if (dateArr[i] >= 1 && dateArr[i] <= 9) {
          dateArr[i] = "0" + dateArr[i];
        }
      }
      var strDate = year+'-'+dateArr[0]+'-'+dateArr[1]+' '+dateArr[2]+':'+dateArr[3]+':'+dateArr[4];
      console.log('strDate',strDate);
      this.article.articleDate=strDate;
    },
    saveArticles (value, render) {
      var _this=this
      // value 是 md，render 是 html
      this.$confirm('是否保存并发布文章?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // _this.getTime();
            this.$axios
                .post('/admin/article', {
                  id: this.article.id,
                  articleTitle: this.article.articleTitle,
                  articleContentMd: value,
                  articleContentHtml: render,
                  articleAbstract: this.article.articleAbstract,
                  articleCover: this.article.articleCover,
                  articleDate: this.article.articleDate
                }).then(resp => {
              if (resp && resp.status === 200) {
                this.$message({
                  type: 'info',
                  message: '已保存成功'
                })
              }
            })
          }
      ).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消发布'
        })
      })
    },
    uploadImg () {
      this.article.articleCover = this.$refs.imgUpload.url
    }
  }
}
</script>

