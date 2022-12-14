package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SdcResourceSatelliteDTO;
import cn.piesat.sec.model.entity.SdcResourceSatelliteDO;
import cn.piesat.sec.model.query.SdcResourceSatelliteQuery;
import cn.piesat.sec.model.vo.SdcResourceSatelliteVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * 卫星基本信息Service接口
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-14 16:35:45
 */
public interface SdcResourceSatelliteService extends IService<SdcResourceSatelliteDO> {

    /**
     * 分页查询
     *
     * @param pageBean                  {@link PageBean} 分页对象
     * @param sdcResourceSatelliteQuery {@link SdcResourceSatelliteQuery} 卫星基本信息查询对象
     * @return {@link PageResult} 查询结果
     */
    PageResult list(PageBean pageBean, SdcResourceSatelliteQuery sdcResourceSatelliteQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SdcResourceSatelliteVO}
     */
    SdcResourceSatelliteVO info(Serializable id);

    /**
     * 获取所有卫星数据
     *
     * @return 所有卫星数据
     */
    List<SdcResourceSatelliteVO> getAllList();

    /**
     * 新增
     *
     * @param sdcResourceSatelliteDTO {@link SdcResourceSatelliteDTO} 卫星基本信息DTO
     * @return false or true
     */
    Boolean save(SdcResourceSatelliteDTO sdcResourceSatelliteDTO);

    /**
     * 修改
     *
     * @param sdcResourceSatelliteDTO {@link SdcResourceSatelliteDTO} 卫星基本信息DTO
     * @return false or true
     */
    Boolean update(SdcResourceSatelliteDTO sdcResourceSatelliteDTO);

    /**
     * 批量删除
     *
     * @param ids id集合
     * @return false or true
     */
    Boolean delete(List<Serializable> ids);

    /**
     * 根据id删除
     *
     * @param id id
     * @return false or true
     */
    Boolean delete(Serializable id);
}

