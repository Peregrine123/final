<template>
  <div>

    <el-button type="primary" icon="el-icon-upload" @click="addMovie">电影导入</el-button>
    <el-dialog
        :modal-append-to-body="false"
        :title=dialogTitle
        :visible.sync="dialogFormVisible"
        @close="clear">
      <el-form v-model="form" style="text-align: left" ref="dataForm">
        <el-form-item label="电影名" :label-width="formLabelWidth" prop="title">
          <el-input v-model="form.title" autocomplete="off" placeholder="不加《》"></el-input>
        </el-form-item>
        <el-form-item label="导演：" :label-width="formLabelWidth" prop="cast">
          <el-input v-model="form.cast" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="上映日期" :label-width="formLabelWidth" prop="date">
          <el-input v-model="form.date" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="发行商：" :label-width="formLabelWidth" prop="press">
          <el-input v-model="form.press" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="封面" :label-width="formLabelWidth" prop="cover">
          <el-input v-model="form.cover" autocomplete="off" placeholder="图片 URL"></el-input>
          <img-upload @onUpload="uploadImg" ref="imgUpload"></img-upload>
          <el-image style="width: 100px; height: 100px" :src="form.cover">
            <div slot="error" class="image-slot"><!--加载失败-->
            </div>
          </el-image>
        </el-form-item>
        <el-form-item label="简介" :label-width="formLabelWidth" prop="summary">
          <el-input type="textarea" v-model="form.summary" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="分类" :label-width="formLabelWidth" prop="cid">
          <el-select v-model="form.category.id" placeholder="请选择分类">
            <el-option label="剧情" value="1"></el-option><!--剧情-->
            <el-option label="喜剧" value="2"></el-option><!--喜剧-->
            <el-option label="爱情" value="3"></el-option><!--爱情-->
            <el-option label="科幻" value="4"></el-option><!--科幻-->
            <el-option label="动作" value="5"></el-option><!--动作-->
            <el-option label="动画" value="6"></el-option><!--动画-->
          </el-select>
        </el-form-item>
        <el-form-item prop="id" style="height: 0">
          <el-input type="hidden" v-model="form.id" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ImgUpload from '../content/ImgUpload'
export default {
  name: 'EditForm',
  components: {ImgUpload},
  data () {
    return {
      dialogFormVisible: false,
      dialogTitle:"添加电影",
      form: {
        id: 0,
        title: '',
        cast: '',
        date: '',
        press: '',
        cover: '',
        summary: '',
        category: {
          id: '',
          name: ''
        }
      },
      formLabelWidth: '120px'
    }
  },
  methods: {
    addMovie(){
      this.dialogTitle="增加电影"
      this.dialogFormVisible = true
    },
    clear () {
      this.$refs.imgUpload.clearFiles()
      this.form = {
        id: 0,
        title: '',
        cast: '',
        date: '',
        press: '',
        cover: '',
        summary: '',
        category: {
          id: '',
          name: ''
        }
      }
    },
    onSubmit () {
      const idNum = parseInt(this.form.id, 10)
      const safeId = isNaN(idNum) ? 0 : idNum
      this.$axios
          .post('/admin/movies', {
            id: safeId,
            cover: this.form.cover,
            title: this.form.title,
            cast: this.form.cast,
            date: this.form.date,
            press: this.form.press,
            summary: this.form.summary,
            category: this.form.category
          }).then(resp => {
        if (resp && resp.status === 200) {
          this.dialogFormVisible = false
          this.$emit('onSubmit')
        }
      })
    },
    uploadImg () {
      this.form.cover = this.$refs.imgUpload.url
    }
  }
}
</script>

<style scoped>
.el-icon-circle-plus-outline {
  margin: 50px 0 0 20px;
  font-size: 100px;
  float: left;
  cursor: pointer;
}
</style>

