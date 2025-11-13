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
            li.setLawd10(MapperUtil.clean(src.lawd10()));
            // lawd5는 DB에 insert할 때 자동으로 생성된다.
            li.setSidoCd(MapperUtil.clean(src.sidoCd()));
            li.setSggCd(MapperUtil.clean(src.sggCd()));
            li.setUmdCd(MapperUtil.clean(src.umdCd()));
            li.setRiCd(MapperUtil.clean(src.riCd()));
            li.setLocatJuminCd(MapperUtil.clean(src.locatJuminCd()));
            li.setLocatJijukCd(MapperUtil.clean(src.locatJijukCd()));
            li.setLocatAddNm(MapperUtil.clean(src.locatAddNm()));
            li.setLocatOrder(MapperUtil.clean(src.locatOrder()));
            li.setLocatRm(MapperUtil.clean(src.locatRm()));
            li.setLocatHighCd(MapperUtil.clean(src.locatHighCd()));
            li.setLocalLowNm(MapperUtil.clean(src.localLowNm()));

            // 변환 결과를 리턴한다.
            return li;
        } catch (Exception e) {
            log.warn(">> [법정동코드] OpenAPI 응답 DTO -> DB 저장용 DTO 변환 실패: {} => {}", src, e.getMessage());
            return null;
        }
    }
}
