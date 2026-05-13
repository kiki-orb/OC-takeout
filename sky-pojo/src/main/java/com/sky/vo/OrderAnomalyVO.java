package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单异常信息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAnomalyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 订单ID
    private Long orderId;

    // 订单号
    private String orderNumber;

    // 订单状态
    private Integer status;

    // 订单状态名称
    private String statusName;

    // 异常类型
    private String anomalyType;

    // 严重程度
    private String severity;

    // 异常原因
    private String reason;

    // 建议处理方式
    private String suggestion;

    // 订单金额
    private BigDecimal amount;

    // 下单时间
    private LocalDateTime orderTime;

    // 结账时间
    private LocalDateTime checkoutTime;

    // 预计送达时间
    private LocalDateTime estimatedDeliveryTime;

    // 延迟分钟数
    private Long delayMinutes;
}