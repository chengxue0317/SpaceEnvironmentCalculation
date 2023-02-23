package cn.piesat.sec.dao.mapper.dataparse;

import cn.piesat.sec.model.vo.dataparse.SoF107VO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 太阳黑子数
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Mapper
public interface SoF107Mapper {
    int save(@Param("data") List<SoF107VO> data) throws Exception;
}
