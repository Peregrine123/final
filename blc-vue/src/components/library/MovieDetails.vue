<template>
  <div class="movie-details">
    <div class="upper-section">
      <div class="cover-container">
        <img :src="movie.cover" alt="封面" class="cover">
      </div>
      <div class="info-container">
        <div class="title">
          <h1>{{ movie.title }}</h1>
        </div>
        <div class="info">
          <p><strong>导演：</strong>{{ movie.cast }}</p>
          <p><strong>上映日期：</strong>{{ movie.date }}</p>
          <p><strong>发行商：</strong>{{ movie.press }}</p>
          <p class="summary"><strong>简介：</strong>{{ movie.summary }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MoviesDetails',
  data() {
    return {
      movie: {},
      movies: [],
    };
  },
  created() {
    this.loadMovieDetails();
  },
  methods: {
    loadMovieDetails() {
      const id = this.$route.params.id
      console.log(id)
      var _this = this
      this.$axios.get('/movies').then(resp => {
        if (resp && resp.status === 200) {
          _this.movies = resp.data
          for (var i = 0; i < _this.movies.length; i++) {
            if (_this.movies[i].id === id) {
              _this.movie = _this.movies[i]
              break
            }
          }
        }
      })

    }
  }
};
</script>

<style scoped>
.movie-details {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  margin: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.upper-section {
  display: flex;
  align-items: flex-start;
}

.cover-container {
  flex: 0 0 40%;
  margin-right: 0px;
  margin-left: 20px;
}

.cover {
  width: 85%;
  height: auto;
  border-radius: 8px;
}

.info-container {
  flex: 0 0 60%;
  margin-left: 7%;
  display: flex;
  flex-direction: column;
}

.title {
  text-align: left;
  display: flex;
  flex: 0 0 25%;
  font-size: 30px;
}


.info {
  flex: 0 0 75%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 10px;
  font-size: 24px;
}

.info p {
  margin-bottom: 1em;
}
.summary{
  max-width: 80%;
}
</style>
