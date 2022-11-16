package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecDstIndexDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DST指数现报数据
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 17:54:18
 */
@Mapper
public interface SecDstIndexMapper extends BaseMapper<SecDstIndexDO> {
    /**
     * 根据时间筛选DST数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return DST数据
     * @throws Exception
     */
    List<SecDstIndexDO> getDSTData(@Param("startTime") String startTime,
        @Param("endTime") String endTime) throws Exception;
}
