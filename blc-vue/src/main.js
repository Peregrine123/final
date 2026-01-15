import Vue from 'vue';
import App from './App.vue';
import router from './router';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import store from './store';
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';

Vue.use(mavonEditor);
Vue.use(ElementUI);


var axios = require('axios');
// Use a relative baseURL so dev/prod can sit behind the same origin (Vue devServer proxy, nginx, etc.).
axios.defaults.baseURL = '/api';
axios.defaults.withCredentials = true;


Vue.prototype.$axios = axios;
Vue.config.productionTip = false;
Vue.config.devtools = true;

// Global auth handling: if backend says 401, clear local user and go to login.
axios.interceptors.response.use(
    resp => resp,
    err => {
        if (err && err.response && err.response.status === 401) {
            store.commit('logout');
            const url = (err.config && err.config.url) ? String(err.config.url) : '';
            // Let the route-guard handle the auth-check request to avoid double redirects.
            const isAuthCheck = url.includes('authentication');
            if (!isAuthCheck && router.currentRoute.path !== '/login') {
                router.push({ path: '/login', query: { redirect: router.currentRoute.fullPath } });
            }
        }
        return Promise.reject(err);
    }
);

Vue.component("card", {
    template: `
    <div class="card-wrap"
      @mousemove="handleMouseMove"
      @mouseenter="handleMouseEnter"
      @mouseleave="handleMouseLeave"
      ref="card">
      <div class="card"
        :style="cardStyle">
        <div class="card-bg" :style="[cardBgTransform, cardBgImage]"></div>
        <div class="card-info">
          <slot name="header"></slot>
          <slot name="content"></slot>
        </div>
      </div>
    </div>`,
    mounted() {
        this.width = this.$refs.card.offsetWidth;
        this.height = this.$refs.card.offsetHeight;
    },
    props: ["dataImage"],
    data: () => ({
        width: 0,
        height: 0,
        mouseX: 0,
        mouseY: 0,
        mouseLeaveDelay: null
    }),
    computed: {
        mousePX() {
            return this.mouseX / this.width;
        },
        mousePY() {
            return this.mouseY / this.height;
        },
        cardStyle() {
            const rX = this.mousePX * 30;
            const rY = this.mousePY * -30;
            return {
                transform: `rotateY(${rX}deg) rotateX(${rY}deg)`
            };
        },
        cardBgTransform() {
            const tX = this.mousePX * -40;
            const tY = this.mousePY * -40;
            return {
                transform: `translateX(${tX}px) translateY(${tY}px)`
            };
        },
        cardBgImage() {
            return {
                backgroundImage: `url(${this.dataImage})`
            };
        }
    },
    methods: {
        handleMouseMove(e) {
            this.mouseX = e.pageX - this.$refs.card.offsetLeft - this.width / 2;
            this.mouseY = e.pageY - this.$refs.card.offsetTop - this.height / 2;
        },
        handleMouseEnter() {
            clearTimeout(this.mouseLeaveDelay);
        },
        handleMouseLeave() {
            this.mouseLeaveDelay = setTimeout(() => {
                this.mouseX = 0;
                this.mouseY = 0;
            }, 1000);
        }
    }
});

// 路由守卫
router.beforeEach((to, from, next) => {
    if (to.meta.requireAuth) {
        if (store.state.user.username !== '') {
            axios.get('/authentication')
                .then(() => next())
                .catch(() => {
                    store.commit('logout');
                    next({
                        path: '/login',
                        query: { redirect: to.fullPath }
                    });
                });
        } else {
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            });
        }
    } else {
        next();
    }
});

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app');
