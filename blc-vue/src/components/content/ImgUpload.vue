<template>
  <el-upload
      class="img-upload"
      ref="upload"
      action="/api/covers"
      with-credentials
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :before-remove="beforeRemove"
      :on-success="handleSuccess"
      :on-error="handleError"
      multiple
      :limit="1"
      :on-exceed="handleExceed"
      :file-list="fileList">
    <el-button size="small" type="primary">点击上传</el-button>
    <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
  </el-upload>
</template>


<script>
export default {
  name: 'ImgUpload',
  data () {
    return {
      fileList: [],
      url: ''
    }
  },
  methods: {
    handleRemove (file, fileList) {
    },
    handlePreview (file) {
    },
    handleExceed (files, fileList) {
      this.$message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    },
    beforeRemove (file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`)
    },
    handleSuccess (response) {
      // alert(response)
      this.url = response
      this.$emit('onUpload')
      this.$message.warning('上传成功')
    },
    handleError(err, file, fileList){
      // alert(err)
      this.url=""
      this.$emit('onUpload')
      this.$message.warning('上传失败')
    },
    clearFiles () {
      this.fileList=[]
    }
  }
}
</script>
