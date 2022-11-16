package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecKpIndexDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * KP指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
@Mapper
public interface SecKpIndexMapper extends BaseMapper<SecKpIndexDO> {
    /**
     * 获取KP指数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return KP指数数据
     * @throws Exception
     */
    List<SecKpIndexDO> getKPData(@Param("startTime") String startTime,
        @Param("endTime") String endTime) throws Exception;

}
