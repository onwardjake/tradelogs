package com.jake.tradelogs.dto.lawd;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/*
응답 구조

StanReginCd
 ├── head (ApiResHeadLawd)
 │    ├── totalCount
 │    ├── numOfRows
 │    ├── pageNo
 │    ├── type
 │    └── result (ApiResultLawd)
 └── rows (List<ApiResRowsLawd>)
 */

@JacksonXmlRootElement(localName = "StanReginCd")
public record ApiResponseLawd(
    @JacksonXmlProperty(localName = "head")
    ApiResHeadLawd head,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    List<ApiResRowsLawd> rows
) {}
