package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecXrayAlarmDTO;
import cn.piesat.sec.model.query.SecXrayAlarmQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecXrayAlarmDO;
import cn.piesat.sec.model.vo.SecXrayAlarmVO;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳X射线耀斑警报事件Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
public interface SecXrayAlarmService extends IService<SecXrayAlarmDO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secXrayAlarmQuery {@link SecXrayAlarmQuery} 太阳X射线耀斑警报事件查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecXrayAlarmQuery secXrayAlarmQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecXrayAlarmVO}
    */
    SecXrayAlarmVO info(Serializable id);

    /**
     * 新增
     *
     * @param secXrayAlarmDTO {@link SecXrayAlarmDTO} 太阳X射线耀斑警报事件DTO
     * @return false or true
    */
    Boolean save(SecXrayAlarmDTO secXrayAlarmDTO);

    /**
     * 修改
     *
     * @param secXrayAlarmDTO {@link SecXrayAlarmDTO} 太阳X射线耀斑警报事件DTO
     * @return false or true
     */
    Boolean update(SecXrayAlarmDTO secXrayAlarmDTO);

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

