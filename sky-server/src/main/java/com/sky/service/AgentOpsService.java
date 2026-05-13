package com.sky.service;

import com.sky.vo.DailyBusinessReportVO;

import java.time.LocalDate;

/**
 * 智能体运维服务接口
 */
public interface AgentOpsService {

    /**
     * 获取营业日报
     * @param date 查询日期，如果为空则默认查询昨天
     * @return 营业日报数据
     */
    DailyBusinessReportVO getDailyReport(LocalDate date);
}
