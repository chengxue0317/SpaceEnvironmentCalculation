package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecParticleFluxDTO;
import cn.piesat.sec.model.query.SecParticleFluxQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecParticleFluxDO;
import cn.piesat.sec.model.vo.SecParticleFluxVO;

import java.io.Serializable;
import java.util.List;

/**
 * 高能粒子通量数据Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
public interface SecParticleFluxService extends IService<SecParticleFluxDO> {
    /**
     * 获取一段时间范围内的质子通量数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 质子通量数据
     */
    SecEnvElementVO getProtonFluxData(String startTime, String endTime, String satId);

    /**
     * 获取一段时间范围内的电子通量数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 电子通量数据
     */
    SecEnvElementVO getElectronicFluxData(String startTime, String endTime, String satId);

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secParticleFluxQuery {@link SecParticleFluxQuery} 高能粒子通量数据查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecParticleFluxQuery secParticleFluxQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecParticleFluxVO}
    */
    SecParticleFluxVO info(Serializable id);

    /**
     * 新增
     *
     * @param secParticleFluxDTO {@link SecParticleFluxDTO} 高能粒子通量数据DTO
     * @return false or true
    */
    Boolean save(SecParticleFluxDTO secParticleFluxDTO);

    /**
     * 修改
     *
     * @param secParticleFluxDTO {@link SecParticleFluxDTO} 高能粒子通量数据DTO
     * @return false or true
     */
    Boolean update(SecParticleFluxDTO secParticleFluxDTO);

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

