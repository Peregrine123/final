import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        user: {
            username: window.localStorage.getItem('user') == null ? '' : JSON.parse(window.localStorage.getItem('user')).username
        }
    },
    mutations: {
        login (state, username) {
            state.user.username = username
            window.localStorage.setItem('user', JSON.stringify(state.user))
        },
        logout (state) {
            state.user = {username: ''}
            window.localStorage.removeItem('user')
        }
    }
})
