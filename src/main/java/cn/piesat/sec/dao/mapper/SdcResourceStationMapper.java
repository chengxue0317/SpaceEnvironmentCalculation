package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SdcResourceStationDO;
import cn.piesat.sec.model.vo.SdcResourceStationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 测站基本信息
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-12-13 14:18:47
 */
@Mapper
public interface SdcResourceStationMapper extends BaseMapper<SdcResourceStationDO> {
    /**
     * 查询所有测站数据
     *
     * @return {@link List < SdcResourceStationVO >} 查询结果
     */
    List<SdcResourceStationVO> getAllList() throws Exception;
}
