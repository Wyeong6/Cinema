package com.busanit.util;

import java.util.HashMap;
import java.util.Map;

//영화 장르의 ID와 이름을 매핑하는 유틸리티 클래스입니다.
public class GenreUtils {

    // genreMap은 장르 ID(Integer)와 장르 이름(String)을 매핑하는 정적 맵입니다.
    private static final Map<Integer, String> genreMap = new HashMap<>();

    // 정적 초기화 블록을 사용하여 genreMap에 장르 ID와 이름을 매핑합니다.
    static {
        genreMap.put(28, "액션");
        genreMap.put(12, "모험");
        genreMap.put(16, "애니메이션");
        genreMap.put(35, "코미디");
        genreMap.put(80, "범죄");
        genreMap.put(99, "다큐멘터리");
        genreMap.put(18, "드라마");
        genreMap.put(10751, "가족");
        genreMap.put(14, "판타지");
        genreMap.put(36, "역사");
        genreMap.put(27, "호러");
        genreMap.put(10402, "음악");
        genreMap.put(9648, "미스터리");
        genreMap.put(10749, "로맨스");
        genreMap.put(878, "SF");
        genreMap.put(10770, "TV 영화");
        genreMap.put(53, "스릴러");
        genreMap.put(10752, "전쟁");
        genreMap.put(37, "서부");
    }

    public static String getGenreName(Integer genreId) {
        return genreMap.getOrDefault(genreId, "알 수 없는 장르");
    }
}
