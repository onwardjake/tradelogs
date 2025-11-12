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
