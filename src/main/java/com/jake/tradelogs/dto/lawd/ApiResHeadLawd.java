package com.jake.tradelogs.dto.lawd;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record ApiResHeadLawd(
    @JacksonXmlProperty(localName = "totalCount") String totalCount,
    @JacksonXmlProperty(localName = "numOfRows") String numOfRows,
    @JacksonXmlProperty(localName = "pageNo") String pageNo,
    @JacksonXmlProperty(localName = "type") String type,
    @JacksonXmlProperty(localName = "RESULT") ApiResHeadResult result
) {}
