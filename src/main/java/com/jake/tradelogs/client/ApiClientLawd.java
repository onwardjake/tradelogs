package com.jake.tradelogs.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.jake.tradelogs.config.ApiPropsLawd;
import com.jake.tradelogs.dto.lawd.ApiResRowsLawd;
import com.jake.tradelogs.dto.lawd.ApiResponseLawd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ApiClientLawd {
    private final ApiPropsLawd props;
    private final XmlMapper xmlMapper = new XmlMapper();

    // Open API 접속을 위한 WebClient 객체를 생성한다.
    public ApiClientLawd(ApiPropsLawd props) {
        this.props = props;
    }

    // Open API에 데이터를 요청해서 받는다.
    // 메소드 오버라이딩을 해서 type과 지역명을 입력하지 않은 경우에 대한 처리를 한다
    public ApiResponseLawd getLawdPage(Integer pageNo, Integer numOfRows){
        return getLawdPage(pageNo, numOfRows, "xml", "");
    }
    public ApiResponseLawd getLawdPage(Integer pageNo, Integer numOfRows, String type, String locataddNm) {

        String resRaw = "";

        try {
            // data.go.kr에서 제공한 java 샘플코드를 사용했음
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1741000/StanReginCd/getStanReginCdList"); //*URL/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + props.serviceKey()); //*Service Key/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo.toString(), "UTF-8")); //*페이지번호/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows.toString(), "UTF-8")); //*한 페이지 결과 수/
            urlBuilder.append("&" + URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")); //*호출문서(xml, json) default : xml/
            urlBuilder.append("&" + URLEncoder.encode("locatadd_nm", "UTF-8") + "=" + URLEncoder.encode(locataddNm, "UTF-8")); //*지역주소명/
            log.info(">> URL: {}", urlBuilder.toString());

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");
            //System.out.println("Response code: " + conn.getResponseCode());
            log.info(">> Response code: {}", conn.getResponseCode());

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            resRaw = sb.toString();
            //System.out.println(sb.toString());
            log.info(">> Response Content: {}", resRaw);
        } catch (Exception e) {
            log.info(">> Open API 요청 처리 중 exception 발생: {}", e.getMessage());
            throw new RuntimeException("Open API 요청 처리 중 exception 발생: " + e.getMessage(), e);
        }

        try {
            // Jackson XmlMapper로 응답을 파싱한다
            log.info(">> Start to parse xml : {}", resRaw);
            return xmlMapper.readValue(resRaw, ApiResponseLawd.class);
        } catch (Exception e) {
            throw new RuntimeException("XML 파싱 실패: " + e.getMessage(), e);
        }
    }

    // 전체 법정동코드를 가지고 온다.
    public List<ApiResRowsLawd> getAllLawd() {
        int pageNo = 1;
        int numOfRows = props.defaultPageSize();
        String type = "xml";
        String locataddNm = "";

        // 첫번째 페이지 데이터를 요청해서 받아온다
        ApiResponseLawd first = getLawdPage(pageNo, numOfRows);
        log.info("first: {}", first);

        List<ApiResRowsLawd> all = new ArrayList<>();

        // 1번째 페이지 데이터를 all에 추가한다.
        if(first != null && !first.rows().isEmpty())
            all.addAll(first.rows());

        // 2번째 페이지부터 마지막 페이지까지 페이지 단위로 데이터를 불러와서 all에 추가한다.
        int total = Optional.of(Integer.parseInt(first.head().totalCount())).orElse(0);
        int pageCount = (int) Math.ceil(total/(double)numOfRows);
        for(pageNo = 2; pageNo <= pageCount; pageNo++) {
            ApiResponseLawd resp = getLawdPage(pageNo, numOfRows);
            if(resp != null && !resp.rows().isEmpty()){
                all.addAll(resp.rows());
            }
        }

        return all;
    }

    // WebClient에서 Request를 보낼 때 로그를 남긴다
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(req -> {
            log.info("WebClient Request: {} {}", req.method(), req.url());
            return Mono.just(req);
        });
    }

    // WebClient에서 Response를 받을 때 로그를 남긴다
    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(res -> {
            log.info("WebClient Response: status={}", res.statusCode());
            return Mono.just(res);
        });
    }
}
