package com.jake.tradelogs.controller;

import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import com.jake.tradelogs.dto.apt.AptTradeFilter;
import com.jake.tradelogs.dto.apt.AptTradeItem;
import com.jake.tradelogs.dto.lawd.SggCdNm;
import com.jake.tradelogs.service.AptService;
import com.jake.tradelogs.service.LawdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/apt/trades")
@RequiredArgsConstructor
public class AptController {
    private final AptService aptService;
    private final LawdService lawdService;

    // http://localhost:8080/apt/trades?sggCd=41430
    @GetMapping
    public String aptTradelist(
            // 페이지네이션 기본값
            @RequestParam(defaultValue = "1") int page, // 페이지 번호
            @RequestParam(defaultValue = "20") int size, // 페이지당 항목 개수
            @RequestParam(defaultValue = "dealDate") String sort,
            @RequestParam(defaultValue = "desc") String dir,

            // 검색 필터
            @ModelAttribute("filter") AptTradeFilter filter,
            Model model) {

        int offset = (page-1) * size;

        // 검색 조건 파라미터 Map 생성
        var params = new HashMap<String, Object>();
        params.put("sggCd", filter.getSggCd());
        params.put("limit", size);
        params.put("offset", offset);
        params.put("sort", sort);
        params.put("dir", dir);

        // 페이지를 구성하기 위해 총 데이터 개수를 조회한다.
        long total = aptService.getCount(params);

        // DB에서 데이터를 읽는다.
        List<AptTradeItem> list = aptService.getAptTradeItems(params);
        List<SggCdNm> sggList = lawdService.getAllSggCdNm();

        // Thymeleaf 모델 전달
        model.addAttribute("list", list);
        model.addAttribute("total", total);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", (int)Math.ceil((double)total / size));
        model.addAttribute("query", params);
        model.addAttribute("sggList", sggList);

        return "apttrade_list";
    }
}
