package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 未来三天警报预报表
 * 
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:48:52
 */
@Mapper
public interface SecAlarmForecastMapper extends BaseMapper<SecAlarmForecastDO> {
	
}
