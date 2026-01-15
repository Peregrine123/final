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
  position: absolute;
  top: 0;
  left: 0;
  margin: 0px;
  padding: 0;
  height: 100%;
  width: 100%;
  z-index: -1;
  background: none;
  overflow: visible;
}
.nav-menu {
  margin: 0;
  box-shadow: 0 2px 4px 0 rgba(0,0,0,.05);
}
</style>
