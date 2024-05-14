package com.busanit.service;

import com.busanit.entity.Movie;
import com.busanit.repository.MovieRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final OkHttpClient client = new OkHttpClient();
    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${TMDB.apiKey}")
    private String apiKey;


    // API를 여러군데서 받아올때 첫번째 에로사항
    // 1. processResponse 가 Movie(엔티티 객체) 를 생성한 후 새롭게생성된 객체에 set 하는 방식이므로
    // 다른 api에서 Movie 객체를 만들어 데이터를 저장할경우 그냥 다른객체 (다른데이터) 가 되버린다
    // ex. api 1 에서 컬럼 1 2 3 을 받아오고 api 2 에서 컬럼 4 5 6 을 받아온다고 치자
    // 그러나 processResponse에서는 엔티티 객체를 새롭게생성한후 set하는 방식이므로
    // processResponse를 두번발동할경우 사실상 컬럼 1 2 3 null,null,null 인 객체
    // 그리고 컬럼 null, null, null, 4,5,6 인 객체가 생기게된다
    // 이것도 해결방법은 있음 그러나 생각을좀해봐야함

    // 두번째 에로사항
    //

    // 문제점 1. processResponse 함수가 Movie객체를 생성해 set하는 방식이기때문에
    // fetchAndStoreMovies 메소드가 발동될때마다 새로운데이터 (즉 중복된데이터) 가 데이터베이스에 계속쌓인다
    // 이건 쉽게 해결가능 JPA레파지토리 save의 특성(값이있으면 update, 없으면 save)을 이용해서 영화 id에 대한 검증을 실시한후
    // 데이터베이스에 save시키는 방향으로 수정하면 쉽게 해결가능함

    @Scheduled(cron = "0 0 * * * *") // 1시간마다 메소드 실행
    public void fetchAndStoreMovies() throws IOException {
        int totalPages = fetchTotalPages();
        for (int page = 1; page <= totalPages; page++) {
            String url = "https://api.themoviedb.org/3/movie/now_playing?language=ko-KR&page=" + page + "&api_key=" + apiKey + "&region=KR";
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                processResponse(responseBody);
            }
        }
    }




    private int fetchTotalPages() throws IOException { // 토탈페이지를 뽑는 함수
        String url = "https://api.themoviedb.org/3/movie/now_playing?language=ko-KR&page=1&api_key=" + apiKey + "&region=KR";
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("total_pages").asInt();
        }
    }

    private void processResponse(String responseBody) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode results = jsonNode.get("results");
        if (results.isArray()) {
            for (JsonNode node : results) {
                Movie movie = new Movie();
                movie.setMovieId(node.get("id").asLong());
                movie.setTitle(node.get("title").asText());
                movie.setOverview(node.get("overview").asText());
                // 필드 추가해야함 지금은 Movie 엔티티(테이블) 이 안만들어져서 임시로 해둠 - 김우영
                movieRepository.save(movie);
            }
        }
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }



}
