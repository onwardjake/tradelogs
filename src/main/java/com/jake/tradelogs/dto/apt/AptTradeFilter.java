package com.jake.tradelogs.dto.apt;

import lombok.Data;

@Data
public class AptTradeFilter {
    private String sggCd;       // 시군구 이름
    private String umdNm;       // 읍면동 이름

    private Integer page = 1;   // 페이지 번호
    private Integer numOfRows = 20;  // 페이지당 개수
    private String sortCatgory = "dealDate";    // 정렬 기준
    private String sortOrder = "desc";          // 정렬 방식

    public int getOffset() {
        return (page - 1) * numOfRows;
    }
}
