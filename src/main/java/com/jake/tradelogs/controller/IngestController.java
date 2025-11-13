package com.jake.tradelogs.controller;

import com.jake.tradelogs.service.AptIngestService;
import com.jake.tradelogs.service.LawdIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/ingest")
@RequiredArgsConstructor
public class IngestController {
    private final LawdIngestService lawdIngestService;
    private final AptIngestService aptIngestService;

    // ================= 아파트 실거래가 =================
    // 전체 지역에 대해 from-to의 기간에 해당하는 데이터를 받아와서 DB에 적재한다.
    // http://localhost:8080/admin/ingest/apt/all?from=202510&to=202511
    @GetMapping("/apt/all")
    public String ingestAptAll(
            @RequestParam String from,
            @RequestParam String to
    ){

        boolean result = aptIngestService.ingestApt(from, to);

        if (result) {
            return "[아파트 실거래가] 아파트 DB 적재가 완료되었습니다. " + "기간: " + from + " ~ " + to;
        }

        return "DB 적재에 실패했습니다.";
    }

    // 특정 지역에 대해 from-to의 기간에 해당하는 데이터를 받아와서 DB에 적재한다.
    // http://localhost:8080/admin/ingest/apt/lawd?lawd=41430&from=202510&to=202511
    @GetMapping("/apt/lawd")
    public String ingestAptLawd(
            @RequestParam String lawd,
            @RequestParam String from,
            @RequestParam String to
    ){
        boolean result = aptIngestService.ingestApt(lawd, from, to);

        if (result) {
            return "[아파트 실거래가] 아파트 DB 적재가 완료되었습니다. " + "LAWD코드: " + lawd + ", 기간: " + from + " ~ " + to;
        }

        return "DB 적재에 실패했습니다.";
    }


    // ================= 법정동코드 =================
    // http://localhost:8080/admin/ingest/lawd/all
    // 주의사항 : 데이터를 전체 요청해서 받아오므로 트래픽이 많이 발생하고 시간이 오래걸림
    @GetMapping("/lawd/all")
    public String ingestLawdAll(){
        // open api에서 전체 데이터를 받아와서 DB에 적재한다.
        lawdIngestService.ingestLawdAll();

        return "법정동 코드 DB 적재가 완료되었습니다.";
    }

    // GetMapping인 경우
    // http://localhost:8080/admin/ingest/lawd/pages?from=1&to=2&numOfRows=100
    //@PostMapping
    @GetMapping("/lawd/pages")
    public String ingestLawdPages(
            @RequestParam(defaultValue = "1") Integer from,
            @RequestParam(defaultValue = "1") Integer to,
            @RequestParam(defaultValue = "5") Integer numOfRows
    ){
        // 데이터를 DB에 적재한다.
        lawdIngestService.ingestLawdPages(from, to, numOfRows);

        // 결과 출력
        StringBuilder result = new StringBuilder()
                .append("[법정동코드] 시작 페이지: ")
                .append(from)
                .append(", 종료 페이지: ")
                .append(to)
                .append(", 페이지당 항목 개수: ")
                .append(numOfRows)
                .append("<br>데이터 적재 완료.");

        return result.toString();
    }
}
