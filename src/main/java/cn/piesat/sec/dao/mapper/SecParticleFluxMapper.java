package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecParticleFluxDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 高能粒子通量数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
@Mapper
public interface SecParticleFluxMapper extends BaseMapper<SecParticleFluxDO> {
    /**
     * 获取质子通量数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 质子通量数据
     * @throws Exception
     */
    List<SecParticleFluxDO> getProtonFluxData(@Param("startTime") String startTime,
                                              @Param("endTime") String endTime) throws Exception;

    /**
     * 获取电子通量数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 质子通量数据
     * @throws Exception
     */
    List<SecParticleFluxDO> getElectronicFluxData(@Param("startTime") String startTime,
                                                  @Param("endTime") String endTime) throws Exception;

}
