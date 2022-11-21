package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecMagneticParamterDTO;
import cn.piesat.sec.model.query.SecMagneticParamterQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecMagneticParamterDO;
import cn.piesat.sec.model.vo.SecMagneticParamterVO;

import java.io.Serializable;
import java.util.List;

/**
 * 地磁参数数据Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
public interface SecMagneticParamterService extends IService<SecMagneticParamterDO> {
    /**
     * 获取一段时间范围内的地磁参数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 地磁参数数据
     */
    SecEnvElementVO getBtxyzData(String startTime, String endTime);

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secMagneticParamterQuery {@link SecMagneticParamterQuery} 地磁参数数据查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecMagneticParamterQuery secMagneticParamterQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecMagneticParamterVO}
    */
    SecMagneticParamterVO info(Serializable id);

    /**
     * 新增
     *
     * @param secMagneticParamterDTO {@link SecMagneticParamterDTO} 地磁参数数据DTO
     * @return false or true
    */
    Boolean save(SecMagneticParamterDTO secMagneticParamterDTO);

    /**
     * 修改
     *
     * @param secMagneticParamterDTO {@link SecMagneticParamterDTO} 地磁参数数据DTO
     * @return false or true
     */
    Boolean update(SecMagneticParamterDTO secMagneticParamterDTO);

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

