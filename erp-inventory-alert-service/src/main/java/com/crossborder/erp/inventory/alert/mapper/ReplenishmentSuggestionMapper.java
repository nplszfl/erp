package com.crossborder.erp.inventory.alert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crossborder.erp.inventory.alert.entity.ReplenishmentSuggestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReplenishmentSuggestionMapper extends BaseMapper<ReplenishmentSuggestion> {

    /**
     * 查询待确认的补货建议
     */
    @Select("SELECT * FROM t_replenishment_suggestion WHERE status = 'PENDING' ORDER BY create_time DESC")
    List<ReplenishmentSuggestion> findPendingSuggestions();

    /**
     * 根据预警ID查询补货建议
     */
    @Select("SELECT * FROM t_replenishment_suggestion WHERE alert_id = #{alertId}")
    ReplenishmentSuggestion findByAlertId(@Param("alertId") Long alertId);
}