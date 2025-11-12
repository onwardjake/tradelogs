package com.jake.tradelogs.dto.lawd;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LawdItem {
    private String lawd10;
    private String lawd5;
    private String sidoCd;
    private String sggCd;
    private String umdCd;
    private String riCd;
    private String locatJuminCd;
    private String locatJijukCd;
    private String locatAddNm;
    private String locatOrder;
    private String locatRm;
    private String locatHighCd;
    private String localLowNm;
    private LocalDate adptDe;
}
