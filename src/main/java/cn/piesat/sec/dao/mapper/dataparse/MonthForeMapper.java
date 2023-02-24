package cn.piesat.sec.dao.mapper.dataparse;

import cn.piesat.sec.model.vo.dataparse.IonoTecVO;
import cn.piesat.sec.model.vo.dataparse.MonthForeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MonthForeMapper {
    int save(@Param("data") List<MonthForeVo> data) throws Exception;
}
