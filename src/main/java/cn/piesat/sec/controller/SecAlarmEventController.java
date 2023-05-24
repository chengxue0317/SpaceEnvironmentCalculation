package cn.piesat.sec.controller;

import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.query.SecAlarmEventQuery;
import cn.piesat.sec.service.SecAlarmEventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * desc
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
@Api(tags = "警报事件")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/alarmevent")
@RequiredArgsConstructor
public class SecAlarmEventController {
    private final SecAlarmEventService alarmEventService;

    @ApiOperation("空间环境警报事件历史数据表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "secAlarmEventQuery", value = "警报事件查询对象", dataType = "String", required = true)
    })
    @OpLog(op = BusinessType.OTHER, description = "空间环境警报事件历史数据")
    @PostMapping("/history")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecAlarmEventQuery secAlarmEventQuery) {
        return alarmEventService.list(pageBean, secAlarmEventQuery);
    }

    @ApiOperation("空间环境警报事件预报数据")
    @OpLog(op = BusinessType.OTHER, description = "空间环境警报事件预报数据")
    @PostMapping("/alarmEvent3daysForecast")
    public PageResult getAlarmEvent3daysForecast() {
        return alarmEventService.getAlarmEvent3daysForecast();
    }
}
