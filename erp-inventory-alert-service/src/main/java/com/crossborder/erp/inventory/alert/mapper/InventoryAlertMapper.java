package com.crossborder.erp.inventory.alert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crossborder.erp.inventory.alert.entity.InventoryAlert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface InventoryAlertMapper extends BaseMapper<InventoryAlert> {

    /**
     * 查询未处理的预警列表
     */
    @Select("SELECT * FROM t_inventory_alert WHERE status = 'PENDING' ORDER BY create_time DESC")
    List<InventoryAlert> findPendingAlerts();

    /**
     * 统计预警数量
     */
    @Select("SELECT COUNT(*) FROM t_inventory_alert WHERE warehouse_id = #{warehouseId} AND create_time >= #{startTime} AND create_time < #{endTime}")
    Integer countAlertsByWarehouseAndTime(@Param("warehouseId") String warehouseId, 
                                           @Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);
}