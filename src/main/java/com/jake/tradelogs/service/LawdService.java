package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientLawd;
import com.jake.tradelogs.dto.lawd.ApiResRowsLawd;
import com.jake.tradelogs.dto.lawd.ApiResponseLawd;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LawdService {
    private final ApiClientLawd apiClientLawd;

    public List<ApiResRowsLawd> getAllLawd() {
        return apiClientLawd.getAllLawd();
    }
}
