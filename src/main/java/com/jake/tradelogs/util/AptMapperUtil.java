package com.jake.tradelogs.util;

import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import com.jake.tradelogs.dto.apt.AptTradeItem;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class AptMapperUtil {
    // API 응답 DTO (ApiResAptBodyItem) → DB 저장용 DTO(AptTradeItem)
    public static AptTradeItem map(ApiResAptBodyItem src) {
        if (src == null)
            return null;

        try {
            // 데이터를 변환한다
            AptTradeItem ati = new AptTradeItem();
            ati.setSggCd(MapperUtil.clean(src.sggCd()));
            ati.setUmdCd(MapperUtil.clean(src.umdCd()));
            ati.setUmdNm(MapperUtil.clean(src.umdNm()));
            ati.setJibun(MapperUtil.clean(src.jibun()));
            ati.setBonbun(MapperUtil.clean(src.bonbun()));
            ati.setBubun(MapperUtil.clean(src.bubun()));

            ati.setRoadNm(MapperUtil.clean(src.roadNm()));
            ati.setRoadNmSggCd(MapperUtil.clean(src.roadNmSggCd()));
            ati.setRoadNmCd(MapperUtil.clean(src.roadNmCd()));
            ati.setRoadNmSeq(MapperUtil.clean(src.roadNmSeq()));
            ati.setRoadNmbCd(MapperUtil.clean(src.roadNmbCd()));
            ati.setRoadNmBonbun(MapperUtil.clean(src.roadNmBonbun()));
            ati.setRoadNmBubun(MapperUtil.clean(src.roadNmBubun()));

            ati.setAptNm(MapperUtil.clean(src.aptNm()));
            ati.setAptSeq(MapperUtil.clean(src.aptSeq()));
            ati.setAptDong(MapperUtil.clean(src.aptDong()));
            ati.setFloor(MapperUtil.clean(src.floor()));
            ati.setExcluUseAr(MapperUtil.clean(src.excluUseAr()));
            ati.setBuildYear(MapperUtil.clean(src.buildYear()));

            ati.setDealYear(Integer.parseInt(MapperUtil.clean(src.dealYear())));
            ati.setDealMonth(Integer.parseInt(MapperUtil.clean(src.dealMonth())));
            ati.setDealDay(Integer.parseInt(MapperUtil.clean(src.dealDay())));
            ati.setDealAmount(Integer.parseInt(MapperUtil.removeComma(MapperUtil.clean(src.dealAmount()))));

            ati.setRgstDate(MapperUtil.parseDate(MapperUtil.clean(src.rgstDate())));
            ati.setDealingGbn(MapperUtil.clean(src.dealingGbn()));
            ati.setEstateAgentSggNm(MapperUtil.clean(src.estateAgentSggNm()));
            ati.setSlerGbn(MapperUtil.clean(src.slerGbn()));
            ati.setBuyerGbn(MapperUtil.clean(src.buyerGbn()));
            ati.setLandLeaseholdGbn(MapperUtil.clean(src.landLeaseholdGbn()));
            ati.setCdealType(MapperUtil.clean(src.cdealType()));
            ati.setCdealDay(MapperUtil.parseDate(MapperUtil.clean(src.cdealDay())));

            // 변환 결과를 리턴한다.
            return ati;
        } catch (Exception e) {
            log.warn(">> [아파트 실거래가] OpenAPI 응답 DTO -> DB 저장용 DTO 변환 실패: {} => {}", src, e.getMessage());
            return null;
        }
    }
}
