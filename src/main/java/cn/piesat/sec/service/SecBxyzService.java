package cn.piesat.sec.service;

import cn.piesat.sec.model.entity.SecBxyzDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 地磁参数数据Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
public interface SecBxyzService extends IService<SecBxyzDO> {
    /**
     * 获取一段时间范围内的地磁参数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 地磁参数数据
     */
    SecEnvElementVO getBtxyzData(String startTime, String endTime);
}

