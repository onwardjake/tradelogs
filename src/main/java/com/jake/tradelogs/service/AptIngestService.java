package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientApt;
import com.jake.tradelogs.mapper.LawdMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AptIngestService {
    private final ApiClientApt apiClientApt;
    //private final AptMapper aptMapper;
    private final LawdMapper lawdMapper;

    @Transactional
    public boolean ingestApt(String from, String to) {
        // lawd 전체를 가지고 온다
        List<String> lawd5s = lawdMapper.getAllLawd5();
        log.info(">> [아파트 실거래가] 총 {}개의 시군구 코드를 대상으로 실거래가를 가져옵니다. 기간: {} ~ {}", lawd5s.size(), from, to);

        // lawd 코드 단위로 데이터를 요청해서 DB에 적재한다
        for (String lawd5 : lawd5s) {
            ingestApt(lawd5, from, to);
        }
        return true;
    }

    @Transactional
    public boolean ingestApt(String lawd, String from, String to) {

        // lawd validation check
        // lawd로 DB에 검색 결과가 없으면 false를 리턴한다.
        String sggNm = lawdMapper.findLawd5(lawd);
        if(sggNm.isEmpty())
            return false;

        log.info(">> [아파트 실거래가] 시군구: {}, Lawd: {}, From: {}, to: {}", sggNm, lawd, from, to);

        // Open
        // from - to 기간만큼 String list로 구성한다

        // list를 interation 하면서 해당 월의 데이터를 받아서 결과 list를 구성한다

        String dealYmd = from;
        //apiClientApt.getAptTradePage(lawd, dealYm, 1, 100);

        // 결과를 DB에 upsert 한다

        return true;
    }
}
