package com.jake.tradelogs.service;

import com.jake.tradelogs.dto.apt.AptTradeFilter;
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

    public long getCount(AptTradeFilter filter) {
        return aptMapper.getCount(filter);
    }

    public List<AptTradeItem> getAptTradeItems(AptTradeFilter filter) {
        return aptMapper.getAptTradeItems(filter);
    }
}
