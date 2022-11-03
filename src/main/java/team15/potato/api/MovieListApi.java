package team15.potato.api;

import kr.or.kobis.kobisopenapi.consumer.rest.KobisOpenAPIRestService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import team15.potato.domain.Movie;
import team15.potato.domain.MovieRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * 영화 상세정보 받아오기
 */

@Component
@RequiredArgsConstructor
public class MovieListApi {

    private final MovieRepository movieRepository;

    // 발급키
    String key = "f4a1f8683e73052acc1311dd6392e58c";

    public void movieList() {
        String movieListResponse = "";

        String itemPerPage = "100";

        /*
         * MovieList에서 받아올 수 있는 값
         * movieCd, movieNm, openDt, repGenreNm, genreAlt, typeNm, directors
         */
        try {
            // kobis Open API 호출
            KobisOpenAPIRestService service = new KobisOpenAPIRestService(key);

            // 영화 목록 호출 (boolean isJson, Map paramMap)
            // 1페이지 당 100개 row -> 1000페이지 갖고옴
            for (int curPage = 1; curPage <= 1000; curPage++) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("curPage", Integer.toString(curPage));
                paramMap.put("itemPerPage", itemPerPage);

                movieListResponse = service.getMovieList(true, paramMap);

                // JSON Parser
                JSONParser jsonParser = new JSONParser();
                Object obj = jsonParser.parse(movieListResponse);
                JSONObject jsonObject = (JSONObject) obj;

                // Parsing
                JSONObject parse_movieListResult = (JSONObject) jsonObject.get("movieListResult");

                /* movieList:[{movieCd, movieNm, openDt, repGenreNm, genreAlt, typeNm, directors}]*/
                JSONArray parse_movieList = (JSONArray) parse_movieListResult.get("movieList");
                for (int i = 0; i < parse_movieList.size(); i++) {
                    JSONObject movie = (JSONObject) parse_movieList.get(i);

                    String directors = "";
                    JSONArray parse_directors = (JSONArray) movie.get("directors");
                    JSONObject director = (JSONObject) parse_directors.get(0);
                    directors = (String) director.get("peopleNm");
                    for (int j = 1; j < parse_directors.size(); j++) {
                        director = (JSONObject) parse_directors.get(j);
                        directors += "|" + (String) director.get("peopleNm");
                    }

                    Movie movieSave = new Movie();

                    movieSave.setMovieCd((String) movie.get("movieCd"));
                    movieSave.setMovieNm((String) movie.get("movieNm"));
                    movieSave.setOpenDt((String) movie.get("openDt"));
                    movieSave.setRepGenreNm((String) movie.get("repGenreNm"));
                    movieSave.setGenreAlt((String) movie.get("genreAlt"));
                    movieSave.setTypeNm((String) movie.get("typeNm"));
                    movieSave.setDirectors(directors);

                    movieRepository.save(movieSave);

                }

            }

        } catch (Exception e) {
            e.getMessage();
        }
    }
}