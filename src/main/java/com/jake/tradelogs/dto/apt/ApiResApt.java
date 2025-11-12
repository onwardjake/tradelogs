package com.jake.tradelogs.dto.apt;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/*
응답 구조

response
 ├── header (ApiResAptHeader)
 │    ├── resultCode
 │    └── resultMsg
 └── body (ApiResAptBody)
      ├── items(List<ApiResAptBody>)
      ├── numOfRows
      ├── pageNo
      └── totalCount
 */

@JacksonXmlRootElement(localName = "response")
public record ApiResApt(
        @JacksonXmlProperty(localName = "header")
        ApiResAptHeader header,

        @JacksonXmlProperty(localName = "body")
        ApiResAptBody body
) {}
