package com.sky.mapper;

import com.sky.vo.TopSalesItemVO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 智能体运维数据访问层
 */
@Mapper
public interface AgentOpsMapper {

    /**
     * 统计总订单数
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单总数
     */
    Integer countTotalOrders(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计有效订单数（已完成）
     * @param begin 开始时间
     * @param end 结束时间
     * @return 有效订单数
     */
    Integer countValidOrders(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计取消订单数
     * @param begin 开始时间
     * @param end 结束时间
     * @return 取消订单数
     */
    Integer countCancelledOrders(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计营业额（已完成订单的金额总和）
     * @param begin 开始时间
     * @param end 结束时间
     * @return 营业额
     */
    BigDecimal sumTurnover(LocalDateTime begin, LocalDateTime end);

    /**
     * 统计新增用户数
     * @param begin 开始时间
     * @param end 结束时间
     * @return 新增用户数
     */
    Integer countNewUsers(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询热销商品列表
     * @param begin 开始时间
     * @param end 结束时间
     * @param limit 限制数量
     * @return 热销商品列表
     */
    List<TopSalesItemVO> selectTopSalesItems(LocalDateTime begin, LocalDateTime end, Integer limit);
}
