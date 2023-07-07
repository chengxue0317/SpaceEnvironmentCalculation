package cn.piesat.sec.service;

import cn.piesat.sec.model.entity.SecApIndexDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * AP指数Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 15:53:01
 */
public interface SecApIndexService extends IService<SecApIndexDO> {
    /**
     * 获取一段时间范围内的AP指数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return AP指数数据
     */
    SecEnvElementVO getApData(String startTime, String endTime);
}

