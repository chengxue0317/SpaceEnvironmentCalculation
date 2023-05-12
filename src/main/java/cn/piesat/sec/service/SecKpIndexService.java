package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecKpIndexDTO;
import cn.piesat.sec.model.query.SecKpIndexQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecKpIndexDO;
import cn.piesat.sec.model.vo.SecKpIndexVO;

import java.io.Serializable;
import java.util.List;

/**
 * KP指数Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
public interface SecKpIndexService extends IService<SecKpIndexDO> {
    /**
     * 获取一段时间范围内的AP指数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return AP指数数据
     */
    SecEnvElementVO getKpData(String startTime, String endTime);
}

