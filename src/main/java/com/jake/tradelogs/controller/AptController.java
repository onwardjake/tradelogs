package com.jake.tradelogs.controller;

import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import com.jake.tradelogs.service.AptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/apttrades")
@RequiredArgsConstructor
public class AptController {
    private final AptService aptService;

    // http://localhost:8080/apttrades?sggCd=41430
    @GetMapping
    public String aptTradelist(
            @RequestParam(required = false) String sggCd,

            // 페이지네이션 기본값
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dealDate") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            Model model) {

        int offset = page * size;

        // 검색 조건 파라미터 Map 생성
        var params = new java.util.HashMap<String, Object> ();
        params.put("sggCd", sggCd);
        params.put("offset", offset);
        params.put("sort", sort);
        params.put("dir", dir);

        // 페이지를 구성하기 위해 개수를 조회한다.
        long total = aptService.getCount(params);

        return "apttrade_list";
    }
}
