package team15.potato.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.kobis.kobisopenapi.consumer.rest.KobisOpenAPIRestService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;
import team15.potato.domain.People;
import team15.potato.domain.PeopleRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * 영화인 목록 받아오는 API
 */

@Component
@RequiredArgsConstructor
public class PeopleListApi {

    private final PeopleRepository peopleRepository;

    // 발급키
    String key = "f4a1f8683e73052acc1311dd6392e58c";

    public void peopleList() {
        String peopleResponse = "";

        String itemPerPage = "100";

        String repRoleNm = "";

        try {
            // Kobis Open API Rest Client를 통해 호출
            KobisOpenAPIRestService service = new KobisOpenAPIRestService(key);

            // 영화인 목록 호출 (boolean isJson, Map paramMap)
            // 1페이지 당 100개 row -> 1000 페이지 갖고옴
            for (int curPage = 1; curPage <= 1000; curPage++) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("curPage", Integer.toString(curPage));
                paramMap.put("itemPerPage", itemPerPage);

                peopleResponse = service.getPeopleList(true, paramMap);

                // JSON Parser
                JSONParser jsonParser = new JSONParser();
                // Parser로 문자열 데이터를 객체로 변환
                Object obj = jsonParser.parse(peopleResponse);
                // 파싱한 obj를 JSONObject 객체로 변환
                JSONObject jsonObject = (JSONObject) obj;

                // Parsing
                /* peopleList:[{peopleCd, peopleNm, peopleNmEn, repRoleNm, filmoNames}] */
                JSONObject parse_peopleListResult = (JSONObject) jsonObject.get("peopleListResult");


                JSONArray parse_peopleList = (JSONArray) parse_peopleListResult.get("peopleList");
                ObjectMapper objectMapper = new ObjectMapper();
                for (int i = 0; i < parse_peopleList.size(); i++) {
                    JSONObject people = (JSONObject) parse_peopleList.get(i);
                    //JSON Object -> Java Object(Entity) 변환
                    People peopleSave = objectMapper.readValue(people.toString(), People.class);

                    //DB 저장 -> repRoleNm이 배우 혹은 감독인 경우만 저장
                    repRoleNm = (String) people.get("repRoleNm");
                    if (repRoleNm.equals("감독") || repRoleNm.equals("배우")) {
                        peopleRepository.save(peopleSave);
                    }
                }

            }

        } catch (Exception e) {
            e.getMessage();
        }
    }
}
