package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.query.SecAlarmEventQuery;

/**
 * desc
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
public interface SecAlarmEventService {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secAlarmEventQuery {@link SecAlarmEventQuery} 太阳质子事件查询对象
     * @return {@link PageResult} 查询结果
     */
    PageResult list(PageBean pageBean, SecAlarmEventQuery secAlarmEventQuery);

    /**
     * 查询未来三天预报事件
     *
     * @return 未来三天预报事件数据
     */
    PageResult getAlarmEvent3daysForecast();
}
