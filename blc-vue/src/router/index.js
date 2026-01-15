import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "../views/Home";

import AppIndex from "../components/home/AppIndex";
import Login from "../views/Login";
import LibraryIndex from "../components/library/LibraryIndex";

import Register from "../views/Register";

import ArticleEditor from "../components/Blog/ArticleEditor";
import Articles from "../components/Blog/Articles";
import ArticleDetails from "../components/Blog/ArticleDetails";
import ArticleManagement from "../components/Blog/ArticleManagement";
import LibraryManagement from "../components/library/LibraryManagement";
import UserProfile from "../components/userProfile/UserProfile";
import EditUserProfile from "../components/userProfile/EditUserProfile";
import CollectionManagement from "../components/library/CollectionManagement";
import AddCollectionManagement from "../components/library/AddCollectionManagement";
import MovieDetails from "@/components/library/MovieDetails";

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'index',
    redirect: '/index',
    component: AppIndex,
    meta: {
      requireAuth: true
    }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    redirect: '/index',
    children: [
      {
        path: '/index',
        name: 'AppIndex',
        component: AppIndex,
      },
      {
        path: '/library',
        name: 'Library',
        component: LibraryIndex,
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/admin/libraryManagement',
        name: 'LibraryManagement',
        component: LibraryManagement,
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/MovieDetails/:id',
        name: 'MovieDetails',
        component: MovieDetails,
        props: true
      },
      {
        path: '/blog',
        name: 'blog',
        component: Articles,
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/blog/article',
        name: 'Article',
        component: ArticleDetails
      },
      {
        path: '/admin/editor',
        name: 'ArticleEditor',
        component: ArticleEditor,
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/admin/ArticleManagement',
        name: 'ArticleManagement',
        component: ArticleManagement,
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/user-profile',
        name:'UserProfile',
        component:UserProfile,
        meta:{
          requireAuth: true
        }
      },
      {
        path: '/edit-user-profile/:id',
        name: 'EditUserProfile',
        component: EditUserProfile,
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/collectionManagement',
        name: 'CollectionManagement',
        component: CollectionManagement,
        meta: {
          requireAuth: true
        }
      },
      {
        path: '/addCollectionManagement',
        name: 'AddCollectionManagement',
        component: AddCollectionManagement,
        meta: {
          requireAuth: true
        }
      },


    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
]

const router = new VueRouter({
  mode: 'history',
  routes
})

export default router
