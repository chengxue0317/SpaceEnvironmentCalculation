package cn.piesat.sec.controller;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.query.AlarmEventQuery;
import cn.piesat.sec.model.vo.AlarmEventVO;
import cn.piesat.sec.service.AlarmEventService;
import cn.piesat.sec.service.ProtonAlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class AlarmEventController {
    private final AlarmEventService alarmEventService;

    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) AlarmEventQuery alarmEventQuery){
        return alarmEventService.list(pageBean,alarmEventQuery);
    }
}
