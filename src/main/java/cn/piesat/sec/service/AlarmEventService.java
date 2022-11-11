package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.query.AlarmEventQuery;
import cn.piesat.sec.model.query.ProtonAlarmQuery;

/**
 * desc
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
public interface AlarmEventService {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param alarmEventQuery {@link AlarmEventQuery} 太阳质子事件查询对象
     * @return {@link PageResult} 查询结果
     */
    PageResult list(PageBean pageBean, AlarmEventQuery alarmEventQuery);
}
