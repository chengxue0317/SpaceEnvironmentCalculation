package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecBxyzDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 地磁参数数据
 * 
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Mapper
public interface SecBxyzMapper extends BaseMapper<SecBxyzDO> {

    /**
     * 获取地磁参数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 地磁参数数据
     * @throws Exception
     */
    List<SecBxyzDO> getBtxyzData(@Param("startTime") String startTime,
                                 @Param("endTime") String endTime) throws Exception;
}
