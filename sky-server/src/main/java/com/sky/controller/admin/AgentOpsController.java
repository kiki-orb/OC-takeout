package com.sky.controller.admin;

import com.sky.dto.OrderAnomalyQueryDTO;
import com.sky.result.Result;
import com.sky.service.AgentOpsService;
import com.sky.service.OrderService;
import com.sky.vo.DailyBusinessReportVO;
import com.sky.vo.OrderAnomalyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 智能体运维接口
 */
@RestController("agentOpsController")
@RequestMapping("/admin/agent")
@Slf4j
@Api(tags = "智能体运维接口")
public class AgentOpsController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AgentOpsService agentOpsService;

    @Value("${sky.agent.token:}")
    private String agentToken;

    /**
     * 查询异常订单列表
     *
     * @param unconfirmedMinutes 未确认超时时间（分钟），默认10
     * @param confirmedMinutes 已确认超时时间（分钟），默认30
     * @param request HTTP请求对象，用于获取请求头
     * @return 异常订单列表
     */
    @GetMapping("/orders/anomalies")
    @ApiOperation("查询异常订单列表")
    public Result<List<OrderAnomalyVO>> queryAnomalies(
            @RequestParam(required = false, defaultValue = "10") Integer unconfirmedMinutes,
            @RequestParam(required = false, defaultValue = "30") Integer confirmedMinutes,
            HttpServletRequest request) {
        
        log.info("查询异常订单，unconfirmedMinutes={}, confirmedMinutes={}", unconfirmedMinutes, confirmedMinutes);
        
        // 1. 验证Token
        String tokenFromRequest = request.getHeader("X-Agent-Token");
        
        // 从环境变量获取Token（优先级更高）
        String envToken = System.getenv("SKY_AGENT_TOKEN");
        String validToken = envToken != null && !envToken.isEmpty() ? envToken : agentToken;
        
        // Token验证
        if (validToken == null || validToken.isEmpty()) {
            log.warn("Agent Token未配置");
            return Result.error("系统配置错误");
        }
        
        if (tokenFromRequest == null || !tokenFromRequest.equals(validToken)) {
            log.warn("Agent Token验证失败");
            return Result.error("认证失败");
        }
        
        // 2. 构建查询参数
        OrderAnomalyQueryDTO queryDTO = OrderAnomalyQueryDTO.builder()
                .unconfirmedMinutes(unconfirmedMinutes)
                .confirmedMinutes(confirmedMinutes)
                .build();
        
        // 3. 调用Service查询异常订单
        List<OrderAnomalyVO> anomalyList = orderService.queryAnomalies(queryDTO);
        
        log.info("查询到异常订单数量：{}", anomalyList.size());
        
        // 4. 返回结果
        return Result.success(anomalyList);
    }

    /**
     * 获取营业日报
     *
     * @param date 查询日期，格式 yyyy-MM-dd，可选；不传则默认查询昨天
     * @param request HTTP请求对象，用于获取请求头
     * @return 营业日报数据
     */
    @GetMapping("/reports/daily")
    @ApiOperation("获取营业日报")
    public Result<DailyBusinessReportVO> getDailyReport(
            @RequestParam(required = false) String date,
            HttpServletRequest request) {
        
        log.info("查询营业日报，date={}", date);
        
        // 1. 验证Token
        String tokenFromRequest = request.getHeader("X-Agent-Token");
        
        // 从环境变量获取Token（优先级更高）
        String envToken = System.getenv("SKY_AGENT_TOKEN");
        String validToken = envToken != null && !envToken.isEmpty() ? envToken : agentToken;
        
        // Token验证
        if (validToken == null || validToken.isEmpty()) {
            log.warn("Agent Token未配置");
            return Result.error("系统配置错误");
        }
        
        if (tokenFromRequest == null || !tokenFromRequest.equals(validToken)) {
            log.warn("Agent Token验证失败");
            return Result.error("认证失败");
        }
        
        // 2. 解析日期参数
        LocalDate queryDate = null;
        if (date != null && !date.isEmpty()) {
            try {
                queryDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                log.warn("日期格式错误：{}", date);
                return Result.error("日期格式错误，请使用 yyyy-MM-dd 格式");
            }
        }
        
        // 3. 调用Service获取营业日报
        DailyBusinessReportVO report = agentOpsService.getDailyReport(queryDate);
        
        log.info("营业日报查询成功，日期：{}", report.getDate());
        
        // 4. 返回结果
        return Result.success(report);
    }
}
