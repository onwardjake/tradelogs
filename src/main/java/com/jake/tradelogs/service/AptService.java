package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientApt;
import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AptService {
    private final ApiClientApt apiClientApt;

    public List<ApiResAptBodyItem> getAllAptTrades() {
        return apiClientApt.getAllAptTrades();
    }

}
