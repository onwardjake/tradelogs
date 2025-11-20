package com.jake.tradelogs.mapper;

import com.jake.tradelogs.dto.apt.AptTradeFilter;
import com.jake.tradelogs.dto.apt.AptTradeItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AptMapper {
    int bulkUpsert(@Param("list") List<AptTradeItem> list);

    long getCount(AptTradeFilter filter);

    List<AptTradeItem> getAptTradeItems(AptTradeFilter filter);
}
