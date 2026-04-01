package com.crossborder.erp.inventory.alert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crossborder.erp.inventory.alert.entity.InventoryAlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InventoryAlertRuleMapper extends BaseMapper<InventoryAlertRule> {

    /**
     * 查询仓库下所有启用的预警规则
     */
    @Select("SELECT * FROM t_inventory_alert_rule WHERE warehouse_id = #{warehouseId} AND enabled = true")
    List<InventoryAlertRule> findEnabledRulesByWarehouse(@Param("warehouseId") String warehouseId);

    /**
     * 查询产品预警规则
     */
    @Select("SELECT * FROM t_inventory_alert_rule WHERE product_id = #{productId} AND warehouse_id = #{warehouseId}")
    List<InventoryAlertRule> findByProductAndWarehouse(@Param("productId") Long productId, @Param("warehouseId") String warehouseId);
}