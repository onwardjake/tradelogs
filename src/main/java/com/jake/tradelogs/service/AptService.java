package com.jake.tradelogs.service;

import com.jake.tradelogs.dto.apt.AptTradeItem;
import com.jake.tradelogs.mapper.AptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AptService {
    private final AptMapper aptMapper;

    public long getCount(Map<String, Object> params) {
        return aptMapper.getCount(params);
    }

    public List<AptTradeItem> getAptTradeItems(Map<String, Object> params) {
        return aptMapper.getAptTradeItems(params);
    }
}
