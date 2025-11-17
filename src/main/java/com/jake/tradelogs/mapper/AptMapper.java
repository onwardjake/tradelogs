package com.jake.tradelogs.mapper;

import com.jake.tradelogs.dto.apt.AptTradeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface AptMapper {
    int bulkUpsert(@Param("list") List<AptTradeItem> list);

    long getCount(HashMap<String, Object> params);
}
