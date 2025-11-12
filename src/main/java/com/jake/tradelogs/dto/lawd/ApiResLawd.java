package com.jake.tradelogs.dto.lawd;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/*
응답 구조

StanReginCd
 ├── head (ApiResLawdHead)
 │    ├── totalCount
 │    ├── numOfRows
 │    ├── pageNo
 │    ├── type
 │    └── result (ApiResLawdHeadResult)
 └── rows (List<ApiResLawdRows>)
 */

@JacksonXmlRootElement(localName = "StanReginCd")
public record ApiResLawd(
    @JacksonXmlProperty(localName = "head")
    ApiResLawdHead head,

    // <row>가 반복될 때 감싸는 요소 없이 바로 나열되므로 useWrapping=false
    // 즉, row들을 감싸주는 상위 요소(ex. <rows><row></row></rpws>없이 <row> 들이 계속 나열되므로 u
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    List<ApiResLawdRows> rows
) {}
