package com.jake.tradelogs.mapper;

import com.jake.tradelogs.dto.lawd.LawdItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LawdMapper {
    int bulkUpsert(@Param("list") List<LawdItem> list);
}
