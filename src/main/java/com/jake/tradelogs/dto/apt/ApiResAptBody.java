package com.jake.tradelogs.dto.apt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResAptBody(
        @JacksonXmlProperty(localName = "items") ApiResAptBodyItems items,
        @JacksonXmlProperty(localName = "numOfRows") Integer numOfRows,
        @JacksonXmlProperty(localName = "pageNo") Integer pageNo,
        @JacksonXmlProperty(localName = "totalCount") Integer totalCount
) {}
