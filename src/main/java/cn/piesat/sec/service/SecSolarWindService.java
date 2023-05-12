package cn.piesat.sec.service;

import cn.piesat.sec.model.entity.SecSolarWindDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 太阳风速Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
public interface SecSolarWindService extends IService<SecSolarWindDO> {
    /**
     * 获取一段时间范围内的太阳风速数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 太阳风速数据
     */
    SecEnvElementVO getSolarWindData(String startTime, String endTime);
}

