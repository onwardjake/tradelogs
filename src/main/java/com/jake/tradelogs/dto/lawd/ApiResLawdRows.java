package com.jake.tradelogs.dto.lawd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResLawdRows(
        @JacksonXmlProperty(localName = "region_cd") String lawd10,
        @JacksonXmlProperty(localName = "sido_cd") String sidoCd,
        @JacksonXmlProperty(localName = "sgg_cd") String sggCd,
        @JacksonXmlProperty(localName = "umd_cd") String umdCd,
        @JacksonXmlProperty(localName = "ri_cd") String riCd,
        @JacksonXmlProperty(localName = "locatjumin_cd") String locatJuminCd, // 지역코드_주민
        @JacksonXmlProperty(localName = "locatjijuk_cd") String locatJijukCd, // 지역코드_지적
        @JacksonXmlProperty(localName = "locatadd_nm") String locatAddNm, // 지역주소명
        @JacksonXmlProperty(localName = "locat_order") String locatOrder,
        @JacksonXmlProperty(localName = "locat_rm") String locatRm, // 비고
        @JacksonXmlProperty(localName = "locathigh_cd") String locatHighCd, // 상위지역코드
        @JacksonXmlProperty(localName = "locallow_nm")  String localLowNm, // 최하위지역명
        @JacksonXmlProperty(localName = "adpt_de") String adptDe // 생성일
) {}
