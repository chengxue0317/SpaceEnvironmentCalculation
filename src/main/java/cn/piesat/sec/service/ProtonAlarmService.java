package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.ProtonAlarmDTO;
import cn.piesat.sec.model.query.ProtonAlarmQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.AlarmEventDO;
import cn.piesat.sec.model.vo.ProtonAlarmVO;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳质子事件Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
public interface ProtonAlarmService extends IService<AlarmEventDO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param protonAlarmQuery {@link ProtonAlarmQuery} 太阳质子事件查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, ProtonAlarmQuery protonAlarmQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link ProtonAlarmVO}
    */
    ProtonAlarmVO info(Serializable id);

    /**
     * 新增
     *
     * @param protonAlarmDTO {@link ProtonAlarmDTO} 太阳质子事件DTO
     * @return false or true
    */
    Boolean save(ProtonAlarmDTO protonAlarmDTO);

    /**
     * 修改
     *
     * @param protonAlarmDTO {@link ProtonAlarmDTO} 太阳质子事件DTO
     * @return false or true
     */
    Boolean update(ProtonAlarmDTO protonAlarmDTO);

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

