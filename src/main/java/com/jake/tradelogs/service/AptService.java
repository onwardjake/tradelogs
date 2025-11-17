package com.jake.tradelogs.service;

import com.jake.tradelogs.client.ApiClientApt;
import com.jake.tradelogs.dto.apt.ApiResAptBodyItem;
import com.jake.tradelogs.mapper.AptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AptService {
    private final AptMapper aptMapper;

    public long getCount(HashMap<String, Object> params) {
        return aptMapper.getCount(params);
    }
}
