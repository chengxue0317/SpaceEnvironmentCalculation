package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecAlarmForecastDTO;
import cn.piesat.sec.model.query.SecAlarmForecastQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import cn.piesat.sec.model.vo.SecAlarmForecastVO;

import java.io.Serializable;
import java.util.List;

/**
 * 未来三天警报预报表Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:48:52
 */
public interface SecAlarmForecastService extends IService<SecAlarmForecastDO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secAlarmForecastQuery {@link SecAlarmForecastQuery} 未来三天警报预报表查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecAlarmForecastQuery secAlarmForecastQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecAlarmForecastVO}
    */
    SecAlarmForecastVO info(Serializable id);

    /**
     * 新增
     *
     * @param secAlarmForecastDTO {@link SecAlarmForecastDTO} 未来三天警报预报表DTO
     * @return false or true
    */
    Boolean save(SecAlarmForecastDTO secAlarmForecastDTO);

    /**
     * 修改
     *
     * @param secAlarmForecastDTO {@link SecAlarmForecastDTO} 未来三天警报预报表DTO
     * @return false or true
     */
    Boolean update(SecAlarmForecastDTO secAlarmForecastDTO);

    /**
     * 批量删除
     *
     * @param ids id集合
     * @return false or true
    */
    Boolean delete(List<Serializable> ids);

    /**
     * 根据id删除
     *
     * @param id id
     * @return false or true
    */
    Boolean delete(Serializable id);
}

