package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecDstIndexDO;
import cn.piesat.sec.model.entity.SecF107FluxDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太阳F10.7指数
 * 
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
@Mapper
public interface SecF107FluxMapper extends BaseMapper<SecF107FluxDO> {
    /**
     * 根据时间筛选F107数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return DST数据
     * @throws Exception
     */
    List<SecF107FluxDO> getF107Data(@Param("startTime") String startTime,
        @Param("endTime") String endTime) throws Exception;
}
