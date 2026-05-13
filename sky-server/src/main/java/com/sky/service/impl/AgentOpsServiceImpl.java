package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.AgentOpsMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.AgentOpsService;
import com.sky.vo.DailyBusinessReportVO;
import com.sky.vo.TopSalesItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 智能体运维服务实现类
 */
@Service
@Slf4j
public class AgentOpsServiceImpl implements AgentOpsService {

    @Autowired
    private AgentOpsMapper agentOpsMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取营业日报
     *
     * @param date 查询日期，如果为空则默认查询昨天
     * @return 营业日报数据
     */
    @Override
    public DailyBusinessReportVO getDailyReport(LocalDate date) {
        // 1. 如果 date 为空，默认查询昨天
        if (date == null) {
            date = LocalDate.now().minusDays(1);
        }

        log.info("查询营业日报，日期：{}", date);

        // 2. 统计时间范围为 date 00:00:00 到 date + 1 天 00:00:00
        LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);

        // 3. 统计各项指标
        // 3.1 总订单数
        Integer totalOrderCount = agentOpsMapper.countTotalOrders(beginTime, endTime);
        totalOrderCount = totalOrderCount == null ? 0 : totalOrderCount;

        // 3.2 有效订单数（已完成）
        Integer validOrderCount = agentOpsMapper.countValidOrders(beginTime, endTime);
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;

        // 3.3 取消订单数
        Integer cancelledOrderCount = agentOpsMapper.countCancelledOrders(beginTime, endTime);
        cancelledOrderCount = cancelledOrderCount == null ? 0 : cancelledOrderCount;

        // 3.4 营业额（已完成订单的实收金额总和）
        BigDecimal turnover = agentOpsMapper.sumTurnover(beginTime, endTime);
        turnover = turnover == null ? BigDecimal.ZERO : turnover;

        // 3.5 新增用户数
        Integer newUserCount = agentOpsMapper.countNewUsers(beginTime, endTime);
        newUserCount = newUserCount == null ? 0 : newUserCount;

        // 3.6 计算比率指标
        Double orderCompletionRate = 0.0;
        Double cancelRate = 0.0;
        BigDecimal unitPrice = BigDecimal.ZERO;

        if (totalOrderCount > 0) {
            // 订单完成率 = validOrderCount / totalOrderCount * 100，保留两位小数
            orderCompletionRate = new BigDecimal(validOrderCount)
                    .divide(new BigDecimal(totalOrderCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            // 取消率 = cancelledOrderCount / totalOrderCount * 100，保留两位小数
            cancelRate = new BigDecimal(cancelledOrderCount)
                    .divide(new BigDecimal(totalOrderCount), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            // 平均客单价 = turnover / validOrderCount，保留两位小数
            if (validOrderCount > 0) {
                unitPrice = turnover.divide(new BigDecimal(validOrderCount), 2, RoundingMode.HALF_UP);
            }
        }

        // 3.7 销量前10商品
        List<TopSalesItemVO> topItems = getTopSalesItems(beginTime, endTime);

        // 3.8 生成简要总结
        String summary = generateSummary(date, turnover, totalOrderCount, validOrderCount, 
                cancelledOrderCount, orderCompletionRate, unitPrice, newUserCount);

        // 4. 封装返回结果
        return DailyBusinessReportVO.builder()
                .date(date.toString())
                .turnover(turnover)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .cancelledOrderCount(cancelledOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .cancelRate(cancelRate)
                .unitPrice(unitPrice)
                .newUserCount(newUserCount)
                .topItems(topItems)
                .summary(summary)
                .build();
    }

    /**
     * 获取销量前10商品
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 热销商品列表
     */
    private List<TopSalesItemVO> getTopSalesItems(LocalDateTime beginTime, LocalDateTime endTime) {
        // 调用 AgentOpsMapper 查询销量前10（包含销售数量和金额）
        List<TopSalesItemVO> topItems = agentOpsMapper.selectTopSalesItems(beginTime, endTime, 10);

        if (topItems == null || topItems.isEmpty()) {
            return new ArrayList<>();
        }

        return topItems;
    }

    /**
     * 生成简要总结
     *
     * @param date                 统计日期
     * @param turnover             营业额
     * @param totalOrderCount      总订单数
     * @param validOrderCount      有效订单数
     * @param cancelledOrderCount  取消订单数
     * @param orderCompletionRate  订单完成率
     * @param unitPrice            平均客单价
     * @param newUserCount         新增用户数
     * @return 简要总结文本
     */
    private String generateSummary(LocalDate date, BigDecimal turnover, Integer totalOrderCount,
                                    Integer validOrderCount, Integer cancelledOrderCount,
                                    Double orderCompletionRate, BigDecimal unitPrice, Integer newUserCount) {
        StringBuilder summary = new StringBuilder();
        summary.append(date.toString()).append(" ");

        // 营业额情况
        if (turnover.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            summary.append("营业额表现优秀，达到").append(turnover).append("元；");
        } else if (turnover.compareTo(BigDecimal.valueOf(5000)) >= 0) {
            summary.append("营业额表现良好，达到").append(turnover).append("元；");
        } else {
            summary.append("营业额为").append(turnover).append("元；");
        }

        // 订单完成情况
        summary.append("共").append(totalOrderCount).append("单，完成").append(validOrderCount).append("单");

        // 完成率评价
        if (orderCompletionRate >= 90) {
            summary.append("，完成率").append(orderCompletionRate).append("%，表现优秀；");
        } else if (orderCompletionRate >= 70) {
            summary.append("，完成率").append(orderCompletionRate).append("%，表现良好；");
        } else {
            summary.append("，完成率").append(orderCompletionRate).append("%，需关注；");
        }

        // 取消订单提示
        if (cancelledOrderCount > 5) {
            summary.append("取消订单").append(cancelledOrderCount).append("单，建议分析取消原因；");
        }

        // 新用户情况
        if (newUserCount > 10) {
            summary.append("新增用户").append(newUserCount).append("人，增长势头良好。");
        } else if (newUserCount > 0) {
            summary.append("新增用户").append(newUserCount).append("人。");
        } else {
            summary.append("无新增用户。");
        }

        return summary.toString();
    }
}
