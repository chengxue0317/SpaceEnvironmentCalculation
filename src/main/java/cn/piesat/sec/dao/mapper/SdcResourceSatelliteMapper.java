package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.SdcResourceSatelliteDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 卫星基本信息
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Mapper
public interface SdcResourceSatelliteMapper extends BaseMapper<SdcResourceSatelliteDO> {
    /**
     * 查询所有测站数据
     *
     * @return {@link List < SdcResourceStationVO >} 查询结果
     */
    List<SdcResourceSatelliteDO> getAllList() throws Exception;
}
