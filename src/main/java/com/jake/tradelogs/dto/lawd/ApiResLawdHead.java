package com.jake.tradelogs.dto.lawd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResLawdHead(
    @JacksonXmlProperty(localName = "totalCount") Integer totalCount,
    @JacksonXmlProperty(localName = "numOfRows") Integer numOfRows,
    @JacksonXmlProperty(localName = "pageNo") Integer pageNo,
    @JacksonXmlProperty(localName = "type") String type,
    @JacksonXmlProperty(localName = "RESULT") ApiResLawdHeadResult result
) {}
