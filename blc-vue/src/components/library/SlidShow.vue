<template>
<div class="swiper-box">
  <div class="pre-btn" @click="changeSwiper(-1)">
    <img src="/static/left.png" alt="">
  </div>
  <div class="next-btn" @click="changeSwiper(1)">
    <img src="/static/right.png" alt="">
  </div>

  <div class="swiper-list" :style="{left:swiperLeft,transition:transition}">
    <div class="swiper-item"
         v-for="(item,index) in collections.slice(0,6)" :key="index"
         :class="{active:tabIndex+2===index}" :style="{ 'background-image': 'url(' + item.cover + ')','background-repeat':'no-repeat','background-size':'auto'}"
    >
      <div style="background: rgba(0,0,0,0.5)">
        <div class="title">{{item.title}}</div>
        <div
          :class="['Multi-line','two-line','content',]">{{item.summary}}</div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
export default {
  name: 'SlidShow',
  data () {
    return {
      collections: [],
      tabIndex: 0,
      swiperList: [],
      swiperIndex: 0,
      transition: 'all 0.3s'
    }
  },
  mounted: function () {
    this.loadCollection()
    this.swiperList = [
      ...this.collections.slice(-2),
      ...this.collections,
      ...this.collections.slice(0, 2)
    ]
  },
  methods: {
    loadCollection () {
      var _this = this
      this.$axios.get('/collections').then(resp => {
        if (resp && resp.status === 200) {
          _this.collections = resp.data
          this.swiperList = [...this.collections]
        }
      })
    },
    changeSwiper (type) {
      // if (!debounce(360)) {
      //   return
      // }
      if (type === 1) {
        console.log(this.swiperIndex)
        if (this.tabIndex === this.collections.length - 1) {
          this.tabIndex = 0
        } else {
          this.tabIndex++
        }

        this.swiperIndex++
        if (this.swiperIndex > this.swiperList.length - this.collections.length + 2) {
          setTimeout(() => {
            this.transition = 'unset'
            this.swiperIndex = 0
          }, 301)
          setTimeout(() => {
            this.transition = 'all 0.3s'
          }, 360)
        }
      } else {
        console.log(this.swiperIndex)
        if (this.tabIndex === 0) {
          this.tabIndex = this.collections.length - 1
        } else {
          this.tabIndex--
        }
        this.swiperIndex--
        if (this.swiperIndex === -3) {
          setTimeout(() => {
            this.transition = 'unset'
            this.swiperIndex = this.swiperList.length - this.collections.length
          }, 301)
          setTimeout(() => {
            this.transition = 'all 0.3s'
          }, 360)
        }
      }
    }
  },
  computed: {
    swiperLeft () {
      return -((this.swiperIndex + 2) * (23 + 3.5) - 4.0) + 'rem'
    }
  }

}
</script>

<style scoped>
.swiper-box {
  height: 40rem;
  position: relative;
  overflow: hidden;
  margin-top: 0.5rem;
}
.swiper-list {
  display: flex;
  width: 8000rem;
  height: 40rem;
  overflow-x: auto;
  position: absolute;
  top: 0;
  left: -30rem;
  transition: all 0.3s;

}
.swiper-item {
  display: flex;
  align-content: flex-end;  /* 修改这里，将 end 改为 flex-end */
  flex-wrap: wrap;
  width:30rem;
  height: 20rem;
  border-radius: 20px;
  margin-right: 0.2rem;
  opacity: 0.6;
  padding: 0 0.95rem;
}


.swiper-item :last-child {
    margin-right: 0;
  }

.swiper-item.active {
    opacity: 1;
  }

.Multi-line{
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  line-clamp: 6;
  -webkit-line-clamp: 6;
}
.two-line{
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  line-clamp: 2;
  -webkit-line-clamp: 2;
}
.pre-btn, .next-btn {
  width: 4rem;
  height: 4rem;
  position: absolute;
  left: 1rem;
  top: 30%;
  transform: translateY(-50%);
  z-index: 9;
  cursor: pointer
}

.pre-btn img {
  width: 100%;
  height: 100%;
}
.next-btn img {
  width: 100%;
  height: 100%;
}
.next-btn{
    left:unset;
    right:1rem;
}
.title{
  font-size: 19px;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 10px;
  margin-left: 23px;
  text-align: center;
}
.content{
  font-weight: bold;
  color: #ffffff;
  font-size: 15px;
  margin-bottom: 10px;
  margin-left: 5px;
  margin-right: 5px;
  width:27rem;
  align-content: center;
  min-height: 34px;
  text-align: justify;
}
</style>
