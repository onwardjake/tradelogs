package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientLawd;
import com.jake.tradelogs.dto.lawd.ApiResLawdRows;
import com.jake.tradelogs.dto.lawd.SggCdNm;
import com.jake.tradelogs.mapper.LawdMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LawdService {
    private final ApiClientLawd apiClientLawd;
    private final LawdMapper lawdMapper;

    public List<ApiResLawdRows> getAllLawd() {
        return apiClientLawd.getAllLawd();
    }

    public List<ApiResLawdRows> getPagesLawd(Integer from, Integer to, Integer numOfRows) {
        return apiClientLawd.getPagesLawd(from, to, numOfRows);
    }

    public List<SggCdNm> getAllSggCdNm() {
        return lawdMapper.getAllSggCdNm();
    }
}
