package com.jake.tradelogs.dto.apt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResAptBody(
        @JacksonXmlProperty(localName = "items") ApiResAptBodyItems items,
        @JacksonXmlProperty(localName = "numOfRows") String numOfRows,
        @JacksonXmlProperty(localName = "pageNo") String pageNo,
        @JacksonXmlProperty(localName = "totalCount") String totalCount
) {}
