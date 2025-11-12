package com.jake.tradelogs.controller;

import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import com.jake.tradelogs.service.AptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/apt")
@RequiredArgsConstructor
public class AptController {
    private final AptService aptService;

    @GetMapping
    public List<ApiResAptBodyItem> getAllAptTrades() {
        return aptService.getAllAptTrades();
    }

    /*
    @GetMapping("/getpage")
    public List<ApiResAptBodyItem> getPage(
            @RequestParam String lawdCd,
            @RequestParam String dealYmd,
            @RequestParam Integer pageNo,
            @RequestParam Integer numOfRows
    ){
        return aptService.getAptTradePage(lawdCd, dealYmd, pageNo, numOfRows);
    }

     */
}
