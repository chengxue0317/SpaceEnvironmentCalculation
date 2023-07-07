package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.log.model.OpLogEntity;
import cn.piesat.kjyy.common.log.service.ExecuteLogService;
import org.springframework.stereotype.Component;

/**
 * 日志记录
 *
 * @author hezhanfeng
 * @date 2023/05/15 10:02
 */
@Component
public class ExecuteLogServiceImpl implements ExecuteLogService {
    @Override
    public void exec(OpLogEntity opLogEntity) {
        // TODO 入库
        System.out.println(opLogEntity);
    }
}
