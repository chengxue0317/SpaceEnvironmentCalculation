package cn.piesat.sec.service;

import cn.piesat.sec.model.vo.SecEnvOverviewVO;

import java.util.List;

/**
 * 获取空间环境预报综述信息
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 17:54:18
 */
public interface SecEnvOverviewService {
    List<SecEnvOverviewVO> getEnvOverview();
}
