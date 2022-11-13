package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecDstAlarmDTO;
import cn.piesat.sec.model.query.SecDstAlarmQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecDstAlarmDO;
import cn.piesat.sec.model.vo.SecDstAlarmVO;

import java.io.Serializable;
import java.util.List;

/**
 * ${comments}Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:47:57
 */
public interface SecDstAlarmService extends IService<SecDstAlarmDO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secDstAlarmQuery {@link SecDstAlarmQuery} ${comments}查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecDstAlarmQuery secDstAlarmQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecDstAlarmVO}
    */
    SecDstAlarmVO info(Serializable id);

    /**
     * 新增
     *
     * @param secDstAlarmDTO {@link SecDstAlarmDTO} ${comments}DTO
     * @return false or true
    */
    Boolean save(SecDstAlarmDTO secDstAlarmDTO);

    /**
     * 修改
     *
     * @param secDstAlarmDTO {@link SecDstAlarmDTO} ${comments}DTO
     * @return false or true
     */
    Boolean update(SecDstAlarmDTO secDstAlarmDTO);

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

