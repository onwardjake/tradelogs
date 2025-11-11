package com.jake.tradelogs.dto.lawd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResHeadResult(
    @JacksonXmlProperty(localName = "resultCode") String resultCode,
    @JacksonXmlProperty(localName = "resultMsg") String resultMsg
) {}
