<template>
  <div class="app">
  <div>
    <!-- 背景动画部分 -->
    <div id="background-animation" ref="background">
      <div
          class="background-image"
          v-for="(image, index) in backgroundImages"
          :key="index"
          :class="{ active: currentImage === index }"
      >
        <img :src="image" @error="console.warn('[bg] load failed', index, image)" />
      </div>
    </div>

    <!-- 中心文字 -->
    <div id="center-content" ref="content">
      <h1>影博汇</h1>
      <h2>FilmTalk</h2>
      <p></p>
      <div class="buttons">

        <router-link to="/blog" class="routerlink">影评</router-link>

        <router-link to="/library" class="routerlink">影视簿</router-link>
      </div>
    </div>
  </div>

    <div class="most">
    <section class="poster">
      <div class="left">不</div>
      <div class="right">不</div>
      <div class="left">知道</div>
      <div class="right">知道</div>
      <div class="left">要看</div>
      <div class="right">要看</div>
      <div class="left">什么？</div>
      <div class="right">什么？</div>
      <div class="left">NO</div>
      <div class="right">idea</div>
      <div class="left">of</div>
      <div class="right">what</div>
      <div class="left">mo</div>
      <div class="right">vies?</div>
      <div></div>
      <div class="last">
        <div>随机</div>
        <div>观影</div>
        <div>Random</div>
        <div>Movies</div>
      </div>

    </section>


<!--      电影卡片-->
      <div class="movieslist1">

          <div class="movie">
            <div class="movie-img"></div>
            <div class="text-movie-cont">
              <div class="mr-grid">
                <div class="col1">
                  <h1>星际穿越Interstellar</h1>
                  <ul class="movie-gen">
                    <li>PG-13  /</li>
                    <li>2h 49min  /</li>
                    <li>科幻</li>
                  </ul>
                </div>
              </div>
              <div class="mr-grid summary-row">
                <div class="col2">
                  <h5>简介</h5>
                  <p></p>
                </div>
              </div>
              <div class="mr-grid">
                <div class="col1">
                  <p class="movie-description">该片在物理学家基普·索恩的黑洞理论之上进行改编，主要讲述了地球农作物因气候转变及枯萎病而经常失收，曾是美国国家航空航天局的工程师和航天飞机驾驶员的库珀（马修·麦康纳饰）被迫成为农民以协助解决粮食危机。 </p>
                </div>
              </div>
              <div class="mr-grid actors-row">
                <div class="col1">
                  <p></p>
                  <p class="movie-actors">马修·麦康纳、安妮·海瑟薇、杰西卡·查斯坦、麦肯吉·弗依 、卡西·阿弗莱克 </p>
                </div>
              </div>
            </div>
          </div>

        <div class="movie">
          <div class="movie-img1"></div>
          <div class="text-movie-cont">
            <div class="mr-grid">
              <div class="col1">
                <h1>寻梦环游记Coco</h1>
                <ul class="movie-gen">
                  <li>PG  /</li>
                  <li>1h 49min  /</li>
                  <li>剧情</li>
                </ul>
              </div>
            </div>
            <div class="mr-grid summary-row">
              <div class="col2">
                <h5>简介</h5>
                <p></p>
              </div>
            </div>
            <div class="mr-grid">
              <div class="col1">
                <p class="movie-description">讲述了一个鞋匠家庭出身的12岁小男孩米格尔，自幼有一个音乐梦，但音乐却是被家庭所禁止的，他们认为自己被音乐诅咒了。在米格尔秘密追寻音乐梦时，不小心进入了死亡之地，在这里他遇见了家人们的灵魂，并得到了他们的祝福去歌唱，最终重返人间的故事。</p>
              </div>
            </div>
            <div class="mr-grid actors-row">
              <div class="col1">
                <p></p>
                <p class="movie-actors">安东尼·冈萨雷兹、本杰明·布拉特、盖尔·加西亚·贝纳尔、蕾妮·维克多、阿莱娜·尤波奇</p>
              </div>
            </div>
          </div>
        </div>


      </div>

    </div>


    <div class="taptonew">
      <router-link to="/library"  class="custom-btn btn-5"><span>了解更多</span></router-link>
    </div>



    <div class="fontset">
      <h1 class="test">
  <span class="word">精选
    <span> </span>
   </span>
        <span class="word">影评</span>
      </h1>

      <div class="movieslist">
        <div id="app" class="container">
          <card :data-image="cardImages[0]">
            <h1 slot="header">白蛇：浮生</h1>
            <p slot="content">爱让我接受最真实的你。
            </p>
          </card>

          <card :data-image="cardImages[1]">
            <h1 slot="header">盗梦空间</h1>
            <p slot="content">它是最好的玄幻小说，因为情节无懈可击。
            </p>
          </card>

          <card :data-image="cardImages[2]">
            <h1 slot="header">楚门的世界</h1>
            <p slot="content">我们每个人都是楚门</p>
          </card>

          <card :data-image="cardImages[3]">
            <h1 slot="header">喜剧之王</h1>
            <p slot="content">飘飘，我来教你怎样拥抱。</p>
          </card>
        </div>
        <p></p>
      </div>
    </div>

    <div class="taptonew1">
      <router-link to="/blog"  class="custom-btn btn-12"><span>请点击!</span><span>阅读更多</span></router-link>
    </div>

    <h1 class='elegantshadow'>FilmTalk_影博汇</h1>
    <p></p>
    <h2 class='elegantshadow'>联系我们</h2>

  </div>
</template>

<script>
import { gsap } from "gsap";
import Card from "@/components/Card.vue";
import bgSina from "@/assets/home-bg/bg-sina.jpg";
import bgZcool from "@/assets/home-bg/bg-zcool.jpg";
import bgBaidu from "@/assets/home-bg/bg-baidu.jpg";
import bgCanva from "@/assets/home-bg/bg-canva.jpg";
import card1 from "@/assets/remote/appindex-card1.jpg";
import card2 from "@/assets/remote/appindex-card2.jpg";
import card3 from "@/assets/remote/appindex-card3.jpg";
import card4 from "@/assets/remote/appindex-card4.jpeg";

export default {
  name: "AppIndex",
  components: {
    Card,
  },
  data() {
    return {
      // Use local assets to avoid hotlink/403 issues and ensure consistent loading.
      backgroundImages: [bgSina, bgZcool, bgBaidu, bgCanva],
      // Local card images (avoid hotlink/403 and make the homepage usable offline).
      cardImages: [card1, card2, card3, card4],
      currentImage: 0,
      _bgTimer: null,
    };
  },
  mounted() {
    // Preload to prevent "blank" frames when switching (some backgrounds are large).
    this.preloadBackgroundImages().then(() => {
      this.startAnimation();
    });
  },
  beforeDestroy() {
    if (this._bgTimer) clearInterval(this._bgTimer);
  },
  methods: {
    preloadBackgroundImages() {
      const urls = Array.isArray(this.backgroundImages) ? this.backgroundImages : [];
      return Promise.all(
        urls.map(
          (src) =>
            new Promise((resolve) => {
              const img = new Image();
              img.onload = () => resolve({ src, ok: true });
              img.onerror = () => resolve({ src, ok: false });
              img.src = src;
            })
        )
      );
    },
    startAnimation() {
      // 背景黑色淡出
      gsap.to(this.$refs.background, {
        duration: 1,
        backgroundColor: "transparent",
      });

      // 文字淡入
      gsap.to(this.$refs.content, {
        duration: 1.5,
        delay: 1,
        opacity: 1,
      });

      // 背景图轮播
      this._bgTimer = setInterval(() => {
        this.currentImage = (this.currentImage + 1) % this.backgroundImages.length;
      }, 5000); // 每 5 秒切换一次
    },
  },
};
</script>

<style scoped>




/* 背景部分样式 */
#background-animation {
  position: relative;
  width: 100%;
  height: 50vh;
  /*background-color: black;*/
  overflow: hidden;
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  filter: blur(3px);
  transition: opacity 1s ease, filter 1s ease;
}

.background-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.background-image.active {
  opacity: 1;
  filter: blur(1px);
}

/* 中心文字内容 */

#center-content {
  position: absolute;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
  opacity: 0;
  z-index: 10;
}

#center-content h1 {
  font-size: 4rem;
  margin-bottom: 1rem;
}

#center-content p {
  margin-bottom: 1.5rem;
}

#center-content .buttons button {
  margin: 0 10px;
  padding: 10px 20px;
  font-size: 1rem;
  color: white;
  background-color: #5d4037;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

#center-content .buttons button:hover {
  background-color: #795548;
}


/* 卡片部分布局 */

.title{
  /*background-color: #1b1f23;*/
  color: black;
}
.container {
  /*background-color: #1b1f23;*/
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  align-items: stretch;
  /*margin: 20px;*/
}

.card {

  width: 250px;
  /*background: black;*/
  border-radius: 8px;
  overflow: hidden;
  /*box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);*/
  text-align: center;
  box-shadow: 0 0 0 1px black;
  transition: transform 0.3s;
}

.card:hover {
  transform: translateY(-10px);
}







/*mostvieew*/
.most{
  display: grid;
  background-color:black;
  grid-template-columns: auto 1fr;
  /*grid-gap: 10px;*/
  width: 100%;
  /*margin-top: 10px;*/
  height: 43rem;
  /*margin-bottom: 10rem;*/
  overflow: hidden;
}

.poster {
  /*background-color: black;*/
  /*box-shadow: 0 0 2rem -.5rem rgba(0, 0, 0, .9);*/
  box-sizing: border-box;
  display: grid;
  font-family: Arial, sans-serif;
  font-weight: 800;
  font-size: 3.15rem;
  grid-template-columns: 5.25rem 18rem;
  height: 45rem;
  line-height: .8;
  /*margin: 3rem 0;*/
  padding: 6rem 6rem 14rem;
  text-transform: uppercase;
  white-space: nowrap;
  width: 28rem;
  overflow: hidden;
}

.left {
  animation: slideleft 5s linear infinite;
  animation-delay: 3s;

  color: white;
  overflow: hidden;
  transform: skewY(-30deg);
  transform-origin: top right;
}
.right {
  animation: slideright 5s linear infinite;
  animation-delay: 3s;

  color: darkgray;
  overflow: hidden;
  text-indent: -5.25rem;
  transform: skewY(30deg);
  transform-origin: top left;
}

.last {
  color: gray;
  font-size: 1.6rem;
  line-height: .9;
  overflow: hidden;
  text-indent: 0;
  transform: skewY(30deg) skewX(-41deg);
  transform-origin: top left;
}
.last > div {
  animation: slidebottom 5s linear infinite;
  animation-delay: 3s;
}

@keyframes slideleft {
  0% { text-indent: -23.25rem; }
  60% { text-indent: 0; }
  100%{ text-indent: 0; }
}

@keyframes slidebottom {
  0% { text-indent: -18rem; }
  25% { text-indent: -18rem; }
  60% { text-indent: 0; }
  100% { text-indent: 0; }
}

@keyframes slideright {
  0% { text-indent: -28.5rem; }
  60% { text-indent: 0; }
  100% { text-indent: -5.25; }
}



.elegantshadow {
  font-family: "Avant Garde", Avantgarde, "Century Gothic", CenturyGothic,
  "AppleGothic", sans-serif;
  /*font-size: 40px;*/
  /*padding: 80px 50px;*/
  text-align: center;
  text-transform: uppercase;
  text-rendering: optimizeLegibility;
  color: #2c2c2c;
  /*background-color: #d5d5d5;*/
  letter-spacing: 0.05em;
  text-shadow: 4px 4px 0px #d5d5d5, 7px 7px 0px rgba(0, 0, 0, 0.2);
 }








.cellphone-container {
  width: 375px;
  height: 650px;
  background-color: #1e1b26;
  margin: 60px auto 0 auto;
  box-shadow: 5px 5px 115px -14px black;
  border-radius: 4px;
}

.menu {
  position: absolute;
  right: 12px;
  top: 6px;
  z-index: 999;
}

.menu i {
  font-size: 40px;
  color: #fe4141;
  filter: drop-shadow(4px 4px 10px rgba(0, 0, 0, 0.5));
}

.movie-img {
  width: 100%;
  height: 380px;
  background-image: url(~@/assets/remote/appindex-poster1.jpg);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  z-index: 111 !important;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  -webkit-mask-image: -webkit-gradient(linear, left top, left bottom, color-stop(0, black), color-stop(0.35, black), color-stop(0.5, black), color-stop(0.65, black), color-stop(0.85, rgba(0, 0, 0, 0.6)), color-stop(1, rgba(0, 0, 0, 0)));
  position: relative;
}

.movie-img1 {
  width: 100%;
  height: 380px;
  background-image: url(~@/assets/remote/appindex-poster2.jpg);
  background-position: center;
  background-repeat: no-repeat;
  background-size: cover;
  z-index: 111 !important;
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  -webkit-mask-image: -webkit-gradient(linear, left top, left bottom, color-stop(0, black), color-stop(0.35, black), color-stop(0.5, black), color-stop(0.65, black), color-stop(0.85, rgba(0, 0, 0, 0.6)), color-stop(1, rgba(0, 0, 0, 0)));
  position: relative;
}

.movieslist1 {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); /* 电影卡片网格布局 */
  grid-auto-rows: 1fr; /* 电影卡片高度自适应内容 */
  gap: 90px; /* 电影卡片之间的间隔 */
  margin-left: 20px; /* 与海报文字的间隔 */
}
.movie {
  background: -moz-linear-gradient(top, rgba(255, 255, 255, 0) 38%, rgba(255, 255, 255, 0.06) 39%, rgba(30, 27, 38, 0.88) 53%, #1e1b26 55%);
  background: -webkit-linear-gradient(top, rgba(255, 255, 255, 0) 38%, rgba(255, 255, 255, 0.06) 39%, rgba(30, 27, 38, 0.88) 53%, #1e1b26 55%);
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0) 38%, rgba(255, 255, 255, 0.06) 39%, rgba(30, 27, 38, 0.88) 53%, #1e1b26 55%);
  filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#00ffffff', endColorstr='#1e1b26',GradientType=0 );
  /*position: absolute;*/
  background-size: contain;
  overflow: hidden;
  background-size: cover;
  z-index: 1 !important;
  width: 100%; /* Responsive width */
  max-width: 375px;
  height: 660px;
  display: block;
  border-radius: 4px;
  margin-right: 20px;
  margin-top: 20px;
}

.text-movie-cont {
  padding: 0px 12px;
  text-align: justify;
}

.action-btn {
  text-align: right;
}

.action-btn i {
  color: #fe4141;
  font-size: 28px;
  text-align: right;
}

.watch-btn {
  display: block;
  border: 1px solid #fe4141;
  border-radius: 5px;
  padding: 4px;
  width: 140px;
}

.watch-btn h3 i {
  font-size: 14px;
  margin-right: 2px;
  position: relative;
  float: left;
  line-height: 1;
}

.action-row {
  margin-top: 24px;
}

.summary-row {
  margin-top: 12px;
}


h1, h2, h3, h4, h5 {
  font-family: "Montserrat", sans-serif;
  color: #e7e7e7;
  margin: 0px;
}

h1 {
  font-size: 36px;
  font-weight: 400;
}

h3 {
  font-size: 14px;
  font-weight: 400;
  color: #fe4141;
}

h5 {
  font-size: 12px;
  font-weight: 400;
}

.movie-gen, .movie-likes {
  margin: 0px;
  padding: 0px;
}

.movie-gen li, .movie-likes li {
  font-family: "Open Sans", sans-serif;
  font-size: 12px;
  color: #818181;
  width: auto;
  display: block;
  float: left;
  margin-right: 6px;
  font-weight: 600;
}

.movie-likes {
  float: right;
}

.movie-likes li {
  color: #fe4141;
}

.movie-likes li:last-child {
  margin-right: 0px;
}

.movie-likes li i {
  font-size: 14px;
  margin-right: 4px;
  position: relative;
  float: left;
  line-height: 1;
}

.movie-details {
  font-family: "Open Sans", sans-serif;
  font-size: 12px;
  font-weight: 300;
  color: #b4b4b4;
}

.movie-description {
  font-family: "Open Sans", sans-serif;
  font-size: 12px;
  font-weight: 400;
  color: #9b9b9b;
  text-align: justify;
  line-height: 1.3;
}

.movie-actors {
  font-family: "Open Sans", sans-serif;
  font-size: 12px;
  font-weight: 300;
  color: #e7e7e7;
  font-style: italic;
}

/* TYPOGRAPHY ENDS */
/** GRID STARTS **/
.thegrid {
  margin: 0 auto;
}

.elements-distance, .movie-details, .movie-description, .movie-actors {
  margin: 0px;
}

.mr-grid {
  width: 100%;
}

.mr-grid:before, .mr-grid:after {
  content: "";
  display: table;
}

.mr-grid:after {
  clear: both;
}

.mr-grid {
  *zoom: 1;
}

.col1, .col2, .col3, .col3rest, .col4, .col4rest, .col5, .col5rest, .col6, .col6rest {
  margin: 0% 0.5% 0.5% 0.5%;
  padding: 1%;
  float: left;
  display: block;
}

/* Columns match, minus margin sum. E.G. margin-left+margin-right=1%, col2=50%-1% - added padding:1%*/
.col1 {
  width: 98%;
}

.col2 {
  width: 47%;
}





/*按钮*/

.slide:hover,
.slide:focus {
  box-shadow: inset 6.5em 0 0 0 var(--hover);
}

.slide {
  --color: #8fc866;
  --hover: #66c887;
}

button {
  color: var(--color);
  transition: 0.25s;
}

button:hover, button:focus {
  border-color: var(--hover);
  color: #fff;
}

button {
  margin-top: 20px;
  background: none;
  border: 2px solid;
  /*font: inherit;*/
  line-height: 1;
  margin: 0.5em;
  padding: 1em 2em;
}



code {
  color: #e4cb58;
  font: inherit;
}


/*字体1*/
:root {
  --bar-scale-y: 0;
  --sparkle-color: rgb(253 244 215 / 40%);
}

@keyframes pop-word {
  to {
    transform: rotateX(0);
  }
}

@keyframes show {
  to {
    opacity: 1;
  }
}

@keyframes bar-scale {
  to {
    transform: scaleY(1);
  }
}

@keyframes sparkle {
  0% {
    transform: scale(0);
  }

  60% {
    transform: scale(1) translate(4px, 1px) rotate(8deg);
  }

  100% {
    transform: scale(0) translate(4px, 1px) rotate(8deg);
  }
}

@keyframes shimmer {
  to {
    text-shadow: 0 0 8px red;
  }
}

.fontset {
  display: grid;
  height: auto;
  grid-template-columns: auto 1fr;
  /*自动宽度和占据*/
  /*background-color: black;*/
  margin-left: 0px;
  margin-right: 0px;
  place-items: start;
  margin-top: 20px;

}

.test {
  color: black;
  font-family: "华文细黑", Vidaloka, serif;
  font-size: 4rem;

  line-height: 0.85;
  perspective: 500px;

  margin-top: 50px;
}

.movieslist{
  margin-left: 20px;
}
.word {
  display: block;

  animation: show 0.5s forwards, pop-word 1.5s forwards;
  animation-timing-function: cubic-bezier(0.14, 1.23, 0.33, 1.16);
  opacity: 0;

  transform: rotateX(120deg);
  transform-origin: 50% 100%;

  margin-top: 10px;
}

.word:nth-of-type(2) {
  padding: 0 2rem;

  animation-delay: 1.5s;

  color: gold;
  margin-left: 80px;
}


/*button new*/
.custom-btn {
  width: 130px;
  height: 40px;
  color: #fff;
  border-radius: 5px;
  padding: 10px 25px;
  font-family: 'Lato', sans-serif;
  font-weight: 500;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
  box-shadow:inset 2px 2px 2px 0px rgba(255,255,255,.5),
  7px 7px 20px 0px rgba(0,0,0,.1),
  4px 4px 5px 0px rgba(0,0,0,.1);
  outline: none;
}


/* 5 */
.btn-5 {
  width: 130px;
  height: 40px;
  line-height: 42px;
  padding: 0;
  border: none;
  background: rgb(255,27,0);
  background: linear-gradient(0deg, rgba(255,27,0,1) 0%, rgba(251,75,2,1) 100%);
}
.btn-5:hover {
  color: white;
  font-size: large;
  background: transparent;
  box-shadow:none;
}
.btn-5:before,
.btn-5:after{
  content:'';
  position:absolute;
  top:0;
  right:0;
  height:2px;
  width:0;
  background: #f0094a;
  box-shadow:
      -1px -1px 5px 0px #fff,
      7px 7px 20px 0px #0003,
      4px 4px 5px 0px #0002;
  transition:400ms ease all;
}
.btn-5:after{
  right:inherit;
  top:inherit;
  left:0;
  bottom:0;
}
.btn-5:hover:before,
.btn-5:hover:after{
  width:100%;
  transition:800ms ease all;
}




/* 12 */
.taptonew{
  height: 5rem;
  width: 100%;
  background-color: black;
}
.taptonew1{
  height: 5rem;
  width: 100%;
  margin-top: 10px;
  background-color:white;
}
.btn-12{
  position: relative;
  right: 20px;
  bottom: 20px;
  border:none;
  box-shadow: none;
  width: 130px;
  height: 40px;
  line-height: 42px;
  -webkit-perspective: 230px;
  perspective: 230px;
}
.btn-12 span {
  background: rgb(255,27,0);
  background: linear-gradient(0deg, rgba(255,27,0,1) 0%, rgba(251,75,2,1) 100%);
  display: block;
  position: absolute;
  width: 130px;
  height: 40px;
  box-shadow:inset 2px 2px 2px 0px rgba(255,255,255,.5),
  7px 7px 20px 0px rgba(0,0,0,.1),
  4px 4px 5px 0px rgba(0,0,0,.1);
  border-radius: 5px;
  margin:0;
  text-align: center;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  -webkit-transition: all .3s;
  transition: all .3s;
}
.btn-12 span:nth-child(1) {
  box-shadow:
      -7px -7px 20px 0px #fff9,
      -4px -4px 5px 0px #fff9,
      7px 7px 20px 0px #0002,
      4px 4px 5px 0px #0001;
  -webkit-transform: rotateX(90deg);
  -moz-transform: rotateX(90deg);
  transform: rotateX(90deg);
  -webkit-transform-origin: 50% 50% -20px;
  -moz-transform-origin: 50% 50% -20px;
  transform-origin: 50% 50% -20px;
}
.btn-12 span:nth-child(2) {
  -webkit-transform: rotateX(0deg);
  -moz-transform: rotateX(0deg);
  transform: rotateX(0deg);
  -webkit-transform-origin: 50% 50% -20px;
  -moz-transform-origin: 50% 50% -20px;
  transform-origin: 50% 50% -20px;
}
.btn-12:hover span:nth-child(1) {
  box-shadow:inset 2px 2px 2px 0px rgba(255,255,255,.5),
  7px 7px 20px 0px rgba(0,0,0,.1),
  4px 4px 5px 0px rgba(0,0,0,.1);
  -webkit-transform: rotateX(0deg);
  -moz-transform: rotateX(0deg);
  transform: rotateX(0deg);
}
.btn-12:hover span:nth-child(2) {
  box-shadow:inset 2px 2px 2px 0px rgba(255,255,255,.5),
  7px 7px 20px 0px rgba(0,0,0,.1),
  4px 4px 5px 0px rgba(0,0,0,.1);
  color: transparent;
  -webkit-transform: rotateX(-90deg);
  -moz-transform: rotateX(-90deg);
  transform: rotateX(-90deg);
}


.routerlink{
   /* 移除超链接的默认样式 */
  margin: 0 10px;
  padding: 10px 20px;
  font-size: 1rem;
  color: white;
  background-color: #5d4037;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-decoration: none;

   font-size: 16px;
   cursor: pointer;
   transition: background-color 0.3s;
 }

.routerlink:hover {
  background-color:  #795548;
}

.routerlink:active {
  background-color: #b3d9ff;
}




</style>
