package com.jake.tradelogs.dto.apt;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AptTradeItem {
    private Long id;
    private String sggNm;
    private LocalDate dealDate;

    private String sggCd;
    private String umdCd;
    private String umdNm;
    private String jibun;
    private String landCd;
    private String bonbun;
    private String bubun;

    private String roadNm;
    private String roadNmSggCd;
    private String roadNmCd;
    private String roadNmSeq;
    private String roadNmbCd;
    private String roadNmBonbun;
    private String roadNmBubun;

    private String aptNm;
    private String aptSeq;
    private String aptDong;
    private String floor;
    private Double excluUseAr;
    private String buildYear;

    private Integer dealYear;
    private Integer dealMonth;
    private Integer dealDay;
    private Integer dealAmount;

    private LocalDate rgstDate;
    private String dealingGbn;
    private String estateAgentSggNm;
    private String slerGbn;
    private String buyerGbn;
    private String landLeaseholdGbn;
    private String cdealType;
    private LocalDate cdealDay;
}
