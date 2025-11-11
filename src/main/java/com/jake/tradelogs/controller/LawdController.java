package com.jake.tradelogs.controller;

import com.jake.tradelogs.dto.lawd.ApiResponseLawd;
import com.jake.tradelogs.service.LawdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lawd")
@RequiredArgsConstructor
public class LawdController {
    private final LawdService lawdService;

    @GetMapping
    public ApiResponseLawd getAllLawd() {
        return lawdService.getAllLawd();
    }
}
