import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        user: {
            username: window.localStorage.getItem('user') == null ? '' : JSON.parse(window.localStorage.getItem('user')).username
        },
        isLoading: false
    },
    mutations: {
        login (state, username) {
            state.user.username = username
            window.localStorage.setItem('user', JSON.stringify(state.user))
        },
        logout (state) {
            state.user = {username: ''}
            window.localStorage.removeItem('user')
        },
        SET_LOADING(state, status) {
            state.isLoading = status
        }
    },
    actions: {
        startLoading({ commit }) {
            commit('SET_LOADING', true)
	        },
	        finishLoading({ commit }) {
	            // Optional: Add a small delay or logic here if needed,
	            // but for now we control smoothness via GSAP in the component
	            commit('SET_LOADING', false)
	        }
	    }
})
