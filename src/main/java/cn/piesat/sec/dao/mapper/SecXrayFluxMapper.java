package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecXrayFluxDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太阳X射线流量
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Mapper
public interface SecXrayFluxMapper extends BaseMapper<SecXrayFluxDO> {
    List<SecXrayFluxDO> getSolarXrayData(@Param("startTime") String startTime,
        @Param("endTime") String endTime) throws Exception;
}
