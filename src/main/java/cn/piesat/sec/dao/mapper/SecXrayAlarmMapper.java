package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SecXrayAlarmDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 太阳X射线耀斑警报事件
 * 
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
@Mapper
public interface SecXrayAlarmMapper extends BaseMapper<SecXrayAlarmDO> {
	
}
