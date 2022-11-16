package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecXrayFluxDTO;
import cn.piesat.sec.model.query.SecXrayFluxQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecXrayFluxDO;
import cn.piesat.sec.model.vo.SecXrayFluxVO;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳X射线流量Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
public interface SecXrayFluxService extends IService<SecXrayFluxDO> {
    /**
     * 获取时间段的太阳黑子数
     * @param startTime
     * @param endTime
     * @return
     */
    EnvElementVO getSolarXrayData(String startTime, String endTime);

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secXrayFluxQuery {@link SecXrayFluxQuery} 太阳X射线流量查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecXrayFluxQuery secXrayFluxQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecXrayFluxVO}
    */
    SecXrayFluxVO info(Serializable id);

    /**
     * 新增
     *
     * @param secXrayFluxDTO {@link SecXrayFluxDTO} 太阳X射线流量DTO
     * @return false or true
    */
    Boolean save(SecXrayFluxDTO secXrayFluxDTO);

    /**
     * 修改
     *
     * @param secXrayFluxDTO {@link SecXrayFluxDTO} 太阳X射线流量DTO
     * @return false or true
     */
    Boolean update(SecXrayFluxDTO secXrayFluxDTO);

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

