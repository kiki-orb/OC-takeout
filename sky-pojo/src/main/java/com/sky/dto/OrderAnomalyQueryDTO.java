package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单异常查询参数DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderAnomalyQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 未确认订单超时时间（分钟）
    private Integer unconfirmedMinutes;

    // 已确认订单超时时间（分钟）
    private Integer confirmedMinutes;
}