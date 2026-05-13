package com.sky.controller.admin;

import com.sky.dto.OrderAnomalyQueryDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderAnomalyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
}
