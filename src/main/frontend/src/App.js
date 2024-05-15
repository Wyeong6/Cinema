import React, {useEffect, useState} from 'react';
import axios from 'axios';

function App() {
  const [movies, setMovies] = useState([]);

  useEffect(() => {
    axios.get('/api/movies')
        .then(response => {
          console.log(response.data); // 데이터 확인
          setMovies(response.data);
        })
        .catch(error => console.log(error));
  }, [])
  return (

      <div>
          {movies.map((movie, index) => (
              <div key={index}>
                  <h2>영화 번호: {movie.id}</h2>
                  <h3>영화 이름: {movie.title}</h3>
                  <p>영화 설명: {movie.overview}</p>
                  <p>영화 장르: {movie.genres.join(', ')}</p>
                  <p>영화 평점: {movie.vote_average}</p>
                  <p>인기도: {movie.popularity}</p>
                  {movie.video && (
                      <div>
                          <p>영화 비디오:</p>
                          <iframe width="500" height="280" src={`https://www.youtube.com/embed/${movie.video}`} frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                      </div>
                  )}
                  <p>영화 배경사진:</p>
                  <img src={`https://image.tmdb.org/t/p/w200${movie.backdropPath}`} alt={`${movie.title} 배경`}/>
                  <p>영화 포스터:</p>
                  <img src={`https://image.tmdb.org/t/p/w200${movie.posterPath}`} alt={`${movie.title} 포스터`}/>
              </div>
          ))}
      </div>
  );
}

export default App;