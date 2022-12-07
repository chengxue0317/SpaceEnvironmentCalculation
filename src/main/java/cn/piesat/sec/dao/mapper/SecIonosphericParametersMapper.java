package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecIonosphericParamtersDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 电离层闪烁数据对象
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
@Mapper
public interface SecIonosphericParametersMapper {

    /**
     * 获取电离层闪烁数据
     *
     * @param staId     站点id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 电离层闪烁数据
     * @throws Exception 异常信息
     */
    List<SecIonosphericParamtersDO> getBlinkData(@Param("staId") String staId,
                                                 @Param("startTime") String startTime,
                                                 @Param("endTime") String endTime) throws Exception;
}
