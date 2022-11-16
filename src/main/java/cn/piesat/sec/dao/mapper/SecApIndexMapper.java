package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecApIndexDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AP指数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 15:53:01
 */
@Mapper
public interface SecApIndexMapper extends BaseMapper<SecApIndexDO> {
    /**
     *获取AP指数数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return kp指数数据
     * @throws Exception
     */
    List<SecApIndexDO> getAPData(@Param("startTime") String startTime,
        @Param("endTime") String endTime) throws Exception;
}
