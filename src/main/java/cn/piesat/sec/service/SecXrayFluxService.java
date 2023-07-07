package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecXrayFluxDTO;
import cn.piesat.sec.model.query.SecXrayFluxQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
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
    SecEnvElementVO getSolarXrayData(String startTime, String endTime);
}

