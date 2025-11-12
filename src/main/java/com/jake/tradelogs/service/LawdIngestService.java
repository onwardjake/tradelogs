package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientLawd;
import com.jake.tradelogs.dto.lawd.ApiResLawdRows;
import com.jake.tradelogs.dto.lawd.LawdItem;
import com.jake.tradelogs.mapper.LawdMapper;
import com.jake.tradelogs.util.LawdMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LawdIngestService {
    private final ApiClientLawd apiClientLawd;
    private final LawdMapper lawdMapper;

    @Transactional
    public void ingestLawdPages(Integer from, Integer to, Integer numOfRows) {
        List<ApiResLawdRows> rows = apiClientLawd.getPagesLawd(from, to, numOfRows);

        // API 응답 DTO (ApiResLawdRows) → DB 저장용 DTO(LawdCode)
        List<LawdItem> items = rows.stream()
                .map(LawdMapperUtil::map)
                .filter(Objects::nonNull)
                .toList();

        // 1000 건씩 분할해서 DB에 upsert 한다.
        int chunk = 1000;
        for(int i=0; i<items.size(); i+=chunk) {
            int end = Math.min(i+chunk, items.size());
            List<LawdItem> sub = items.subList(i, end);
            int affected = lawdMapper.bulkUpsert(sub);
            log.info(">> [법정동코드] Upserted {} rows ({}~{} of {}, 시작페이지={}, 종료페이지={})", affected, i+1, end, items.size(), from, to);
        }
    }
}
