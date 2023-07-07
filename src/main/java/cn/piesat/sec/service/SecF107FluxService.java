package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecF107FluxDTO;
import cn.piesat.sec.model.query.SecF107FluxQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecF107FluxDO;
import cn.piesat.sec.model.vo.SecF107FluxVO;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳F10.7指数Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
public interface SecF107FluxService extends IService<SecF107FluxDO> {

    /**
     * 获取一段时间范围内的F10.7数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return F10.7指数数据
     */
    SecEnvElementVO getF107Data(String startTime, String endTime);
}

