package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 营业日报VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBusinessReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 统计日期
    private String date;

    // 营业额
    private BigDecimal turnover;

    // 总订单数
    private Integer totalOrderCount;

    // 有效订单数（已完成）
    private Integer validOrderCount;

    // 取消订单数
    private Integer cancelledOrderCount;

    // 订单完成率（%）
    private Double orderCompletionRate;

    // 取消率（%）
    private Double cancelRate;

    // 平均客单价
    private BigDecimal unitPrice;

    // 新增用户数
    private Integer newUserCount;

    // 销量前10商品
    private List<TopSalesItemVO> topItems;

    // 简要总结
    private String summary;
}
