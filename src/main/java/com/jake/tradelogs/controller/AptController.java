package com.jake.tradelogs.controller;

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
            // 검색 필터
            @ModelAttribute("filter") AptTradeFilter filter,
            Model model
    ) {
        // 페이지를 구성하기 위해 총 데이터 개수를 조회한다.
        long total = aptService.getCount(filter);

        // DB에서 데이터를 읽는다.
        List<AptTradeItem> list = aptService.getAptTradeItems(filter);
        List<SggCdNm> sggList = lawdService.getAllSggCdNm();

        // Thymeleaf 모델 전달
        model.addAttribute("list", list);
        model.addAttribute("sggList", sggList);
        model.addAttribute("total", total);
        model.addAttribute("totalPages", (int)Math.ceil((double)total / filter.getNumOfRows()));

        return "apttrade_list";
    }
}
