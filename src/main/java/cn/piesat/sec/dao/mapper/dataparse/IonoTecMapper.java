package cn.piesat.sec.dao.mapper.dataparse;

import cn.piesat.sec.model.vo.dataparse.DayForeVo;
import cn.piesat.sec.model.vo.dataparse.IonoTecVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface IonoTecMapper {
    int save(@Param("data") List<IonoTecVO> data) throws Exception;
}
