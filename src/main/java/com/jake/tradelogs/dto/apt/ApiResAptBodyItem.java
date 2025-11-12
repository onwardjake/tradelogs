package com.jake.tradelogs.dto.apt;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record ApiResAptBodyItem(
        // ================== 주소 정보 ==================
        @JacksonXmlProperty(localName = "sggCd") String sggCd,
        @JacksonXmlProperty(localName = "umdCd") String umdCd,
        @JacksonXmlProperty(localName = "umdNm") String umdNm,
        @JacksonXmlProperty(localName = "jibun") String jibun,
        @JacksonXmlProperty(localName = "landCd") String landCd,
        @JacksonXmlProperty(localName = "bonbun") String bonbun,
        @JacksonXmlProperty(localName = "bubun") String bubun,

        // ================== 도로명 정보 ==================
        @JacksonXmlProperty(localName = "roadNm") String roadNm,
        @JacksonXmlProperty(localName = "roadNmSggCd") String roadNmSggCd,
        @JacksonXmlProperty(localName = "roadNmCd") String roadNmCd,
        @JacksonXmlProperty(localName = "roadNmSeq") String roadNmSeq,
        @JacksonXmlProperty(localName = "roadNmbCd") String roadNmbCd,
        @JacksonXmlProperty(localName = "roadNmBonbun") String roadNmBonbun,
        @JacksonXmlProperty(localName = "roadNmBubun") String roadNmBubun,

        // ================== 아파트 정보 ==================
        @JacksonXmlProperty(localName = "aptNm") String aptNm,
        @JacksonXmlProperty(localName = "aptSeq") String aptSeq,
        @JacksonXmlProperty(localName = "aptDong") String aptDong,
        @JacksonXmlProperty(localName = "floor") String floor,
        @JacksonXmlProperty(localName = "excluUseAr") String excluUseAr,
        @JacksonXmlProperty(localName = "buildYear") String buildYear,

        // ================== 계약 정보 ==================
        @JacksonXmlProperty(localName = "dealYear") String dealYear,
        @JacksonXmlProperty(localName = "dealMonth") String dealMonth,
        @JacksonXmlProperty(localName = "dealDay") String dealDay,
        @JacksonXmlProperty(localName = "dealAmount") String dealAmount,

        // ================== 거래 상태/기타 ==================
        @JacksonXmlProperty(localName = "rgstDate") String rgstDate,
        @JacksonXmlProperty(localName = "dealingGbn") String dealingGbn,
        @JacksonXmlProperty(localName = "estateAgentSggNm") String estateAgentSggNm,
        @JacksonXmlProperty(localName = "slerGbn") String slerGbn,
        @JacksonXmlProperty(localName = "buyerGbn") String buyerGbn,
        @JacksonXmlProperty(localName = "landLeaseholdGbn") String landLeaseholdGbn,
        @JacksonXmlProperty(localName = "cdealType") String cdealType,
        @JacksonXmlProperty(localName = "cdealDay") String cdealDay
) {}
