package com.test.weather.service;

import com.test.weather.domain.DateWeather;
import com.test.weather.domain.Diary;
import com.test.weather.repository.DateWeatherRepository;
import com.test.weather.repository.DiaryRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser; // *
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiaryService {

    @Value("${openweathermap.key}") //application.yml 에 저장해 놓고 가져와서 쓰기!!!
    private String apiKey;
    private final DiaryRepository diaryRespository;
    private final DateWeatherRepository dateWeatherRepository;

    public DiaryService(DiaryRepository diaryRespository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRespository = diaryRespository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    // 매일 한 시마다 캐싱을 위한 날씨데이터를 저장하는 스케쥴링 작성
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi(){
        // open weather map 에서 날씨 데이터 가져오기 ***
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherData);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parseWeather.get("temp"));

        return dateWeather;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text){

        // 날씨 데이터 가져오기 (API 에서 or DB에서)
        DateWeather dateWeather = getDateWeather(date);

        // 파싱된 데이터 + 일기 값 우리 db에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);

        diaryRespository.save(nowDiary);
    }

    private DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);

        // 새로 api 에서 날씨 정보를 가져와야함 -> 오늘 날씨 정보로 대체해서 서버에서 가져오기로한다.
        if(dateWeatherListFromDB.size() == 0){
            return getWeatherFromApi();
        }else{
            return dateWeatherListFromDB.get(0);
        }
    }

    @Transactional(readOnly = true) // readOnly = true 로 해줌으로써 속도성능을 높여준다.
    public List<Diary> readDiary(LocalDate date){
        return diaryRespository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate){
        return diaryRespository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text){
        Diary nowDiary = diaryRespository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRespository.save(nowDiary); // 객체속에 id의 값이 같이 주어지므로 새로운 것이 생성되는 것이 아니라 수정이 됨
    }

    public void deleteDiary(LocalDate date){
        diaryRespository.deleteAllByDate(date);
    }


    private String getWeatherString(){ // open weather map 에서 날씨 데이터 가져오기! : *해당 함수 다시 공부*
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else{
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
            br.close();
            return response.toString();

        }catch (Exception e){
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        }catch (ParseException e){
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        return resultMap;
    }

}
