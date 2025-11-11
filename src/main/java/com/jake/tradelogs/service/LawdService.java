package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientLawd;
import com.jake.tradelogs.dto.lawd.ApiResponseLawd;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LawdService {
    private final ApiClientLawd apiClientLawd;

    public ApiResponseLawd getAllLawd() {
        return apiClientLawd.getAllLawd();
    }
}
