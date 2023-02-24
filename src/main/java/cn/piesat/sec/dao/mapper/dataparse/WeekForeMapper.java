package cn.piesat.sec.dao.mapper.dataparse;

import cn.piesat.sec.model.vo.dataparse.IonoTecVO;
import cn.piesat.sec.model.vo.dataparse.WeekForeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface WeekForeMapper {
    int save(@Param("data") List<WeekForeVo> data) throws Exception;
}
