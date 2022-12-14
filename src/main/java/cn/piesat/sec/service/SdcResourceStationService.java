package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SdcResourceStationDTO;
import cn.piesat.sec.model.entity.SdcResourceStationDO;
import cn.piesat.sec.model.query.SdcResourceStationQuery;
import cn.piesat.sec.model.vo.SdcResourceStationVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * 测站基本信息Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-12-13 14:18:47
 */
public interface SdcResourceStationService extends IService<SdcResourceStationDO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param sdcResourceStationQuery {@link SdcResourceStationQuery} 测站基本信息查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SdcResourceStationQuery sdcResourceStationQuery);

    /**
     * 查询所有测站数据
     *
     * @return {@link List<SdcResourceStationVO>} 查询结果
    */
    List<SdcResourceStationVO> getAllList();

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SdcResourceStationVO}
    */
    SdcResourceStationVO info(Serializable id);

    /**
     * 新增
     *
     * @param sdcResourceStationDTO {@link SdcResourceStationDTO} 测站基本信息DTO
     * @return false or true
    */
    Boolean save(SdcResourceStationDTO sdcResourceStationDTO);

    /**
     * 修改
     *
     * @param sdcResourceStationDTO {@link SdcResourceStationDTO} 测站基本信息DTO
     * @return false or true
     */
    Boolean update(SdcResourceStationDTO sdcResourceStationDTO);

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

