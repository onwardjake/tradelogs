package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientLawd;
import com.jake.tradelogs.dto.lawd.ApiResLawdRows;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LawdService {
    private final ApiClientLawd apiClientLawd;

    public List<ApiResLawdRows> getAllLawd() {
        return apiClientLawd.getAllLawd();
    }

    public List<ApiResLawdRows> getPagesLawd(Integer from, Integer to, Integer numOfRows) {
        return apiClientLawd.getPagesLawd(from, to, numOfRows);
    }
}
