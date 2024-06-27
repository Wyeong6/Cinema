package com.busanit.controller;

import com.busanit.domain.movie.ActorDTO;
import com.busanit.domain.movie.MovieDTO;
import com.busanit.domain.SnackDTO;
import com.busanit.entity.movie.Movie;
import com.busanit.entity.movie.MovieActor;
import com.busanit.repository.MovieActorRepository;
import com.busanit.service.MovieService;
import com.busanit.service.SnackService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService2;
    private final ResourceLoader resourceLoader;
    private final SnackService snackService;

    @Value("${upload.path}")
    private String uploadPath;

    @Transactional
    @GetMapping("/")
    public String getDetailMovies(Model model, @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        //비디오가 있는 인기순영화
        List<MovieDTO> videoMovies = movieService2.getCachedVideoMovies();
        model.addAttribute("videoMovies", videoMovies);

        //모든 영화
        List<MovieDTO> allMovies = movieService2.getCachedAllMovies();

        //모든 영화에서 개봉일자가 4개월 전 부터 오늘날짜 까지인거만 가져옴 즉 현재 상영작임.
        List<MovieDTO> filteredMovies = movieService2.getFilteredMovies(allMovies,false);
        model.addAttribute("allMovies", filteredMovies);

        //모든 영화에서 개봉일자가 오늘날짜부터 4개월 후 까지인거만 가져옴 즉 상영예정작(개봉예정)임.
        List<MovieDTO> filteredUpcomingMovies = movieService2.getFilteredMovies(allMovies, true);
        model.addAttribute("upcomingMovies", filteredUpcomingMovies);

        //인기순
        List<MovieDTO> hotMovies = movieService2.getCachedHotMovies();
        model.addAttribute("hotMovies", hotMovies);

        //스낵스토어
        Page<SnackDTO> snackDTOList = null;
        snackDTOList = snackService.getSnackList(pageable);
        model.addAttribute("snackList", snackDTOList);

        return "main";
    }

    //현재 상영작 페이지

    @GetMapping("/nowMovies")
    public String nowMovies(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size) {
        Page<MovieDTO> moviePage = movieService2.getCurrentMoviesPagingAndSorting(page, size);
        model.addAttribute("moviePage", moviePage);
        return "movie/movie_list_now";
    }

    @GetMapping("/comingMovies")
    public String upcomingMovies(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "12") int size) {
        Page<MovieDTO> upcomingMoviesPage = movieService2.getUpcomingMoviesPagingAndSorting(page, size);
        model.addAttribute("moviePage", upcomingMoviesPage);
        return "movie/movie_list_comming";
    }

    @GetMapping("/allMovies")
    public String allMovies(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "12") int size) {
        Page<MovieDTO> allMoviesPage = movieService2.getAllMoviesPagingAndSorting(page, size);
        model.addAttribute("moviePage", allMoviesPage);
        return "movie/movie_list_full";
    }

    //디테일페이지
    @GetMapping("/movies/{movieId}")
    public String movieDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        System.out.println("movieInfos === " + movieInfos);
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        model.addAttribute("userEmail", userEmail);

        return "movie/movie_get";
    }

    @GetMapping("/upcoming/{movieId}")
    public String upcomingDetailinfo(@PathVariable("movieId") Long movieId, Model model) {
        List<MovieDTO> movieInfos = movieService2.getMovieDetailInfo(movieId);
        System.out.println("movieInfos === " + movieInfos.get(0).getStillCutPaths());
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("movieInfos", movieInfos);
        model.addAttribute("movieId", movieId);
        model.addAttribute("userEmail", userEmail);


        return "movie/movie_get";
    }


    @GetMapping("/movies/get")
    public String test(Model model) {
        return "movie/movie_get";
    }

    // 리뷰작성 모달
    @RequestMapping("/review/{movieId}")
    public String reviewPopup(@PathVariable("movieId") String movieId, Model model) {
        String userEmail = movieService2.getUserEmail();

        model.addAttribute("userEmail", userEmail);
        model.addAttribute("movieId", movieId);
        return "movie/review_modal";
    }

    // 검색 기능
    @GetMapping("/search")
    public String searchMovies(@RequestParam("query") String query, Model model) {
        List<MovieDTO> searchResults = movieService2.searchMovies(query);
        model.addAttribute("searchResults", searchResults);
        return "movie/movie_search";
    }

    // 영화 등록 (어드민페이지에서)
    @PostMapping("/movies/regist")
    public ResponseEntity<String> registMovie(
            @RequestParam("id") Long id,
            @RequestParam("title") String movieTitle,
            @RequestParam("overview") String movieOverview,
            @RequestParam("certifications") String certifications,
            @RequestParam("releaseDate") String movieReleaseDate,
            @RequestParam(value = "RegisteredPoster", required = false) MultipartFile registeredPoster,
            @RequestParam(value = "RegisteredBackdrop", required = false) MultipartFile registeredBackdrop,
            @RequestParam("runtime") String runtime,
            @RequestParam("video") String video,
            @RequestParam("genres") List<String> genres,
            @RequestParam(value = "RegisteredStillCut", required = false) List<MultipartFile> registeredStillCut,
            @RequestParam("actors") List<Long> actors,
            Model model
    ) throws IOException {

        System.out.println("actors 체크 === " + actors);

        System.out.println("수정할때 movieId 체크 === " + id);

        String stillCutRelativeUploadDir = "uploads/stillCuts/";
        String backdropRelativeUploadDir = "uploads/backdrops/";
        String posterRelativeUploadDir = "uploads/posters/";

        // 실제 파일 시스템 경로를 설정합니다.
        String uploadDirectory = resourceLoader.getResource("classpath:/static").getFile().getAbsolutePath();
        // 디렉토리 생성
        movieService2.createDirectories(uploadDirectory, stillCutRelativeUploadDir, backdropRelativeUploadDir, posterRelativeUploadDir);

        List<String> stillCutFiles = null;
        String posterRelativeFilePath = null;
        String backdropRelativeFilePath = null;

        System.out.println("stillCutFiles 체크 === " + stillCutFiles);
        System.out.println("posterRelativeFilePath 체크 === " + posterRelativeFilePath);
        System.out.println("backdropRelativeFilePath 체크 === " + backdropRelativeFilePath);
        System.out.println("registeredStillCut 체크 === " + registeredStillCut);
        System.out.println("registeredPoster 체크 === " + registeredPoster);
        System.out.println("registeredBackdrop 체크 === " + registeredBackdrop);


        try {
            if (registeredStillCut != null) {
                stillCutFiles = movieService2.saveStillCutImages(registeredStillCut, uploadDirectory, stillCutRelativeUploadDir);
            }
            if (registeredPoster != null) {
                posterRelativeFilePath = movieService2.saveImage(registeredPoster, uploadDirectory, posterRelativeUploadDir);
            }
            if (registeredBackdrop != null) {
                backdropRelativeFilePath = movieService2.saveImage(registeredBackdrop, uploadDirectory, backdropRelativeUploadDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "파일 저장 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 중 오류가 발생했습니다.");
        }

        try {
            movieService2.saveMovie(
                    id, movieTitle, movieOverview, movieReleaseDate, certifications,
                    posterRelativeFilePath, backdropRelativeFilePath, stillCutFiles, genres, video, runtime, actors
            );
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 영화 ID입니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }

        model.addAttribute("message", "영화 정보가 성공적으로 등록되었습니다.");
        return ResponseEntity.ok("영화 정보가 성공적으로 등록되었습니다.");
    }

    // 영화 삭제 (어드민 페이지에서)
    @PostMapping("/movies/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteMovieAndAddToBlacklist(@PathVariable("id") Long movieId) {
        try {
            movieService2.deleteMovie(movieId);
            movieService2.addToBlacklist(movieId);

            return ResponseEntity.ok("영화가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("영화 삭제 중 오류가 발생했습니다.");
        }
    }

    // 영화 수정 폼을 불러오는 핸들러 (어드민 페이지에서)
    @GetMapping("/movies/edit/{id}")
    public String showEditMovieForm(@PathVariable("id") Long movieId, Model model) {
        Movie movie = movieService2.getMovieById(movieId);
        model.addAttribute("movie", movie); // 수정할 영화 객체를 모델에 추가
        return "admin/admin_movie_register"; // 등록 폼을 재활용
    }


    // 아이디 중복확인 버튼용
    @GetMapping("/movies/checkId")
    @ResponseBody
    public Map<String, Boolean> checkMovieId(@RequestParam("id") String id) {
        Map<String, Boolean> response = new HashMap<>();
        boolean exists = movieService2.checkIfMovieIdExists(id);
        response.put("exists", exists);
        return response;
    }

    // 영화등록시 배우 검색
    @GetMapping("/actors/search")
    @ResponseBody
    public ResponseEntity<List<MovieActor>> searchActors(@RequestParam String query) {

        List<MovieActor> actors = movieService2.findByActorNameContaining(query);


        return ResponseEntity.ok(actors);
    }

    // 직접등록한 영화 상세보기에 배우정보 가져오기

    @GetMapping("/actors/{movieId}")
    @ResponseBody
    public List<Long> getActorIdsByMovieId(@PathVariable("movieId") String movieId) {
        // movieId를 기반으로 해당 영화에 출연한 배우들의 ID 목록을 가져오는 서비스 메서드 호출
        return movieService2.getActorIdsByMovieId(movieId);
    }

    @GetMapping("/actor/{actorId}")
    @ResponseBody
    public ResponseEntity<ActorDTO> getActorById(@PathVariable Long actorId) {
        ActorDTO actorDto = movieService2.getActorById(actorId);
        if (actorDto != null) {
            return ResponseEntity.ok(actorDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}







