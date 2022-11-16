package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecSsnDTO;
import cn.piesat.sec.model.query.SecSsnQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecSsnDO;
import cn.piesat.sec.model.vo.SecSsnVO;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳黑子数Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
public interface SecSsnService extends IService<SecSsnDO> {

    /**
     * 获取时间段的太阳黑子数
     * @param startTime
     * @param endTime
     * @return
     */
    EnvElementVO getSunSpotData(String startTime, String endTime);

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secSsnQuery {@link SecSsnQuery} 太阳黑子数查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecSsnQuery secSsnQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecSsnVO}
    */
    SecSsnVO info(Serializable id);

    /**
     * 新增
     *
     * @param secSsnDTO {@link SecSsnDTO} 太阳黑子数DTO
     * @return false or true
    */
    Boolean save(SecSsnDTO secSsnDTO);

    /**
     * 修改
     *
     * @param secSsnDTO {@link SecSsnDTO} 太阳黑子数DTO
     * @return false or true
     */
    Boolean update(SecSsnDTO secSsnDTO);

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

