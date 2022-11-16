package cn.piesat.sec.service;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.sec.model.vo.EnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecDstIndexDO;

import java.util.List;

/**
 * DST指数现报数据Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 17:54:18
 */
public interface SecDstIndexService extends IService<SecDstIndexDO> {

    /**
     * 获取一段时间范围内的Dst指数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return DST指数数据
     */
    EnvElementVO getDstData(String startTime, String endTime);

    PageResult list(PageBean pageBean, SecDstIndexDO secDstIndexDO);

    SecDstIndexDO info(Long id);

    Boolean add(SecDstIndexDO secDstIndexDO);

    Boolean update(SecDstIndexDO secDstIndexDO);

    Boolean delete(List<Long> asList);

    Boolean delete(Long id);
}

