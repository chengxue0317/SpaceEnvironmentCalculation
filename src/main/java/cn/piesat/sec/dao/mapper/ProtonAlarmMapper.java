package cn.piesat.sec.dao.mapper;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.entity.AlarmEventDO;
import cn.piesat.sec.model.query.AlarmEventQuery;
import cn.piesat.sec.model.vo.AlarmEventVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 太阳质子事件
 * 
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Mapper
public interface ProtonAlarmMapper extends BaseMapper<AlarmEventDO> {
}
