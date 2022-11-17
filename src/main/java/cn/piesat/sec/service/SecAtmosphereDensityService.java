package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecAtmosphereDensityDTO;
import cn.piesat.sec.model.query.SecAtmosphereDensityQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecAtmosphereDensityDO;
import cn.piesat.sec.model.vo.SecAtmosphereDensityVO;

import java.io.Serializable;
import java.util.List;

/**
 * 大气密度预报模块Service接口
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-16 14:58:35
 */
public interface SecAtmosphereDensityService extends IService<SecAtmosphereDensityDO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secAtmosphereDensityQuery {@link SecAtmosphereDensityQuery} 大气密度预报模块查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecAtmosphereDensityQuery secAtmosphereDensityQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecAtmosphereDensityVO}
    */
    SecAtmosphereDensityVO info(Serializable id);

    /**
     * 新增
     *
     * @param secAtmosphereDensityDTO {@link SecAtmosphereDensityDTO} 大气密度预报模块DTO
     * @return false or true
    */
    Boolean save(SecAtmosphereDensityDTO secAtmosphereDensityDTO);

    /**
     * 修改
     *
     * @param secAtmosphereDensityDTO {@link SecAtmosphereDensityDTO} 大气密度预报模块DTO
     * @return false or true
     */
    Boolean update(SecAtmosphereDensityDTO secAtmosphereDensityDTO);

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

