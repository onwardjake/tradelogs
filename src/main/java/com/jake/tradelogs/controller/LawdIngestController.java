package com.jake.tradelogs.controller;

import com.jake.tradelogs.service.LawdIngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/ingest/lawd")
@RequiredArgsConstructor
public class LawdIngestController {
    private final LawdIngestService lawdIngestService;

    @GetMapping("/all")
    public String ingestLawdAll(){
        // open api에서 모든 데이터를 받아와서 DB에 적재한다.
        lawdIngestService.ingestLawdAll();

        return "법정동 코드 DB 적재가 완료되었습니다.";
    }

    //@PostMapping
    @GetMapping
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
