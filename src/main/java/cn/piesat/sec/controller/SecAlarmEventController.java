package cn.piesat.sec.controller;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.query.SecAlarmEventQuery;
import cn.piesat.sec.service.SecAlarmEventService;
import io.swagger.annotations.Api;
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

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) SecAlarmEventQuery secAlarmEventQuery) {
        return alarmEventService.list(pageBean, secAlarmEventQuery);
    }

    @ApiOperation("查询过去24小时及未来3天预报数据")
    @PostMapping("/alarmEvent3daysForecast")
    public PageResult getAlarmEvent3daysForecast() {
        return alarmEventService.getAlarmEvent3daysForecast();
    }
}
