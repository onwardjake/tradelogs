package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientApt;
import com.jake.tradelogs.dto.apt.ApiResApt;
import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import com.jake.tradelogs.dto.apt.AptTradeItem;
import com.jake.tradelogs.dto.lawd.LawdItem;
import com.jake.tradelogs.mapper.AptMapper;
import com.jake.tradelogs.mapper.LawdMapper;
import com.jake.tradelogs.util.AptMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AptIngestService {
    private final ApiClientApt apiClientApt;
    private final AptMapper aptMapper;
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

        // 받아온 모든 데이터를 담아둘 list
        List<ApiResAptBodyItem> apiResAptBodyItems = new ArrayList<>();

        log.info(">> [아파트 실거래가] 시군구: {}, Lawd: {}, From: {}, to: {}", sggNm, lawd, from, to);

        // from - to 기간만큼 String list로 구성한다
        List<String> ranges = ymRange(from, to);

        // 기간 list를 interation 하면서 해당 월의 데이터를 받아서 결과 list를 구성한다
        int pageNo = 1;
        Integer numOfRows = 100;
        int totalCount = 0;
        int pageCount = 0;
        for(String dealYm : ranges) {
            ApiResApt resp = apiClientApt.getAptTradePage(lawd, dealYm, pageNo, numOfRows);
            // 데이터를 리스트에 추가한다
            if(resp != null && resp.body().items() != null && resp.body().items().item() != null) {
                apiResAptBodyItems.addAll(resp.body().items().item());
            }

            // 전체 데이터 개수 및 가져올 회수(페이지)를 계산한다
            totalCount = Optional.ofNullable(resp.body().totalCount()).orElse(0);
            pageCount = (int) Math.ceil(totalCount/(double)numOfRows);

            // 페이지 수 만큼 데이터를 요청한다
            for(pageNo = 2; pageNo <= pageCount; pageNo++) {
                resp = apiClientApt.getAptTradePage(lawd, dealYm, pageNo, numOfRows);

                // 데이터를 리스트에 추가한다
                if(resp != null && resp.body().items() != null && !resp.body().items().item().isEmpty()) {
                    apiResAptBodyItems.addAll(resp.body().items().item());
                }
            }
        }

        // API 응답 DTO (ApiResAptBodyItem) → DB 저장용 DTO(AptTradeItem)
        List<AptTradeItem> aptTradeItems = apiResAptBodyItems.stream()
                .map(AptMapperUtil::map)
                .filter(Objects::nonNull)
                .toList();

        if(aptTradeItems.isEmpty())
            return false;

        // 결과를 DB에 upsert 한다
        // 1000 건씩 분할해서 DB에 upsert 한다.
        int chunk = 1000;
        for(int i=0; i<aptTradeItems.size(); i+=chunk) {
            int end = Math.min(i+chunk, aptTradeItems.size());
            List<AptTradeItem> sub = aptTradeItems.subList(i, end);
            int affected = aptMapper.bulkUpsert(sub);
            log.info(">> [아파트 실거래가] Upserted {} rows ({}~{} of {}, 시작페이지={}, 종료페이지={})", affected, i+1, end, aptTradeItems.size(), from, to);
        }

        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // method : ymRange
    // description : 시작 YYYYMM, 종료 YYYYMM 데이터를 받아서 시작, 종료 기간 사이의 월을 List로 만든다.
    // parameter : fromYm - 시작 거래년월(ex. 202510), toYm - 종료 거래년월
    // return type : 시작 ~ 종료 기간의 월(ex. 202501 ~ 202510) String list를 리턴한다.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    private List<String> ymRange(String fromYm, String toYm) {
        // YYYYMM 데이터에 DD ("01")을 추가한 다음, YYYYMMDD 형태의 날짜 데이터로 변환한다.
        LocalDate from = LocalDate.parse(fromYm + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate to = LocalDate.parse(toYm + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 종료일이 시작일보다 빠르면 시작 YYYYMM를 리턴한다
        if(from.isBefore(to)) {
            return List.of(fromYm);
        }

        // 시작월부터 종료월까지 월 단위로 만들어서 list에 추가한다
        List<String> list = new ArrayList<>();
        LocalDate cur = from.withDayOfMonth(1); // from 객체의 일자를 1로 바꾼 새로운 객체를 반환한다. (ex. 2025-11-10 -> 2025-11-01)
        while (!cur.isAfter(to)) {
            list.add(cur.format(DateTimeFormatter.ofPattern("yyyyMM")));
            cur = cur.plusMonths(1);
        }

        return list;
    }
}
