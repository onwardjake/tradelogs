package com.jake.tradelogs.controller;

import com.jake.tradelogs.dto.lawd.ApiResLawdRows;
import com.jake.tradelogs.service.LawdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lawd")
@RequiredArgsConstructor
public class LawdController {
    private final LawdService lawdService;

    @GetMapping
    public List<ApiResLawdRows> getAllLawd() {
        return lawdService.getAllLawd();
    }

    @GetMapping("/getpages")
    public List<ApiResLawdRows> getPagesLawd(@RequestParam Integer from, @RequestParam Integer to, @RequestParam Integer numOfRows) {
        return lawdService.getPagesLawd(from, to, numOfRows);
    }
}
