package com.jake.tradelogs.util;

import com.jake.tradelogs.dto.lawd.ApiResLawdRows;
import com.jake.tradelogs.dto.lawd.LawdItem;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LawdMapperUtil {
    // API 응답 DTO (ApiResLawdRows) → DB 저장용 DTO(LawdCode)
    public static LawdItem map(ApiResLawdRows src){
        if(src == null)
            return null;

        try{
            // 데이터를 변환한다
            LawdItem li = new LawdItem();
            li.setLawd10(clean(src.lawd10()));
            // lawd5는 DB에 insert할 때 자동으로 생성된다.
            li.setSidoCd(clean(src.sidoCd()));
            li.setSggCd(clean(src.sggCd()));
            li.setUmdCd(clean(src.umdCd()));
            li.setRiCd(clean(src.riCd()));
            li.setLocatJuminCd(clean(src.locatJuminCd()));
            li.setLocatJijukCd(clean(src.locatJijukCd()));
            li.setLocatAddNm(clean(src.locatAddNm()));
            li.setLocatOrder(clean(src.locatOrder()));
            li.setLocatRm(clean(src.locatRm()));
            li.setLocatHighCd(clean(src.locatHighCd()));
            li.setLocalLowNm(clean(src.localLowNm()));

            // 변환 결과를 리턴한다.
            return li;
        } catch (Exception e) {
            log.warn(">> [법정동코드] OpenAPI 응답 DTO -> DB 저장용 DTO 변환 실패: {} => {}", src, e.getMessage());
            return null;
        }
    }

    // ================= 유틸 메서드 =================

    private static String clean(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static LocalDate parseDate(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            s = s.trim().replaceAll("[^0-9]", "");
            if (s.length() == 8)
                return LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
