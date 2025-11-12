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
    public void ingestLawdAll() {
        try{
            Integer page = 205;
            Integer numOfRows = 100;
            while(true){
                // 한 페이지씩 데이터를 불러서 DB에 계속 적재한다.
                // 읽은 데이터가 없다면 loop를 종료한다.
                if(!ingestLawdPages(page, page, numOfRows))
                    break;
                page++;

            }
        } catch (Exception e) {
            log.debug(">> [LawdIngestService::ingestLawdAll] Exception: {}", e.getMessage());
        }
    }

    @Transactional
    public boolean ingestLawdPages(Integer from, Integer to, Integer numOfRows) {
        List<ApiResLawdRows> rows = apiClientLawd.getPagesLawd(from, to, numOfRows);

        // API 응답 DTO (ApiResLawdRows) → DB 저장용 DTO(LawdCode)
        List<LawdItem> items = rows.stream()
                .map(LawdMapperUtil::map)
                .filter(Objects::nonNull)
                .toList();

        if(items.isEmpty()){
            return false;
        }

        // 1000 건씩 분할해서 DB에 upsert 한다.
        int chunk = 1000;
        for(int i=0; i<items.size(); i+=chunk) {
            int end = Math.min(i+chunk, items.size());
            List<LawdItem> sub = items.subList(i, end);
            int affected = lawdMapper.bulkUpsert(sub);
            log.info(">> [법정동코드] Upserted {} rows ({}~{} of {}, 시작페이지={}, 종료페이지={})", affected, i+1, end, items.size(), from, to);
        }

        return true;
    }

}
