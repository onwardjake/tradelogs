package com.jake.tradelogs.dto.lawd;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record ApiResHeadResult(
    @JacksonXmlProperty(localName = "resultCode") String resultCode,
    @JacksonXmlProperty(localName = "resultMsg") String resultMsg
) {}
