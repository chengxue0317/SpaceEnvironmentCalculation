package cn.piesat.sec.dao.mapper.dataparse;

import cn.piesat.sec.model.vo.dataparse.DayForeVo;
import cn.piesat.sec.model.vo.dataparse.IonoTecVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DayForeMapper {
    int save(@Param("data") List<DayForeVo> data) throws Exception;

    int saveFore(@Param("data") List<DayForeVo> data) throws Exception;
}
