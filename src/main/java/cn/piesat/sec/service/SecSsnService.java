package cn.piesat.sec.service;

import cn.piesat.sec.model.entity.SecSsnDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;

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
    SecEnvElementVO getSunSpotData(String startTime, String endTime);
}

