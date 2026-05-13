package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 热销商品VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSalesItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 商品名称
    private String itemName;

    // 销售数量
    private Integer salesCount;

    // 销售金额
    private BigDecimal salesAmount;
}
