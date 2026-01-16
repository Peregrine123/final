<template>
  <div class="bg3">
    <nav-menu class="nav-menu"></nav-menu>
    <router-view/>
  </div>
</template>
<script>

import NavMenu from "../components/common/NavMenu";
export default {
  components: {NavMenu}
}
</script>

<style>
.bg3{
  min-height: 100vh;
  width: 100%;
  margin: 0;
  padding: 0;
  background: none;
  /* background-attachment: fixed; if there was a background image, but here it is none */
}
.nav-menu {
  margin: 0;
  box-shadow: 0 2px 4px 0 rgba(0,0,0,.05);
}
</style>
