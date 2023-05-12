package cn.piesat.sec.service;

import cn.piesat.sec.model.entity.SecParticleFluxDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;

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
    SecEnvElementVO getProtonFluxData(String startTime, String endTime);

    /**
     * 获取一段时间范围内的电子通量数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 电子通量数据
     */
    SecEnvElementVO getElectronicFluxData(String startTime, String endTime);
}

