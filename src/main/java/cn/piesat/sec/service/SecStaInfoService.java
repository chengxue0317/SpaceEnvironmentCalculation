package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecStaInfoDTO;
import cn.piesat.sec.model.query.SecStaInfoQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecStaInfoDO;
import cn.piesat.sec.model.vo.SecStaInfoVO;

import java.io.Serializable;
import java.util.List;

/**
 * 台站信息表Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-27 10:51:47
 */
public interface SecStaInfoService extends IService<SecStaInfoDO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secStaInfoQuery {@link SecStaInfoQuery} 台站信息表查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecStaInfoQuery secStaInfoQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecStaInfoVO}
    */
    SecStaInfoVO info(Serializable id);

    /**
     * 新增
     *
     * @param secStaInfoDTO {@link SecStaInfoDTO} 台站信息表DTO
     * @return false or true
    */
    Boolean save(SecStaInfoDTO secStaInfoDTO);

    /**
     * 修改
     *
     * @param secStaInfoDTO {@link SecStaInfoDTO} 台站信息表DTO
     * @return false or true
     */
    Boolean update(SecStaInfoDTO secStaInfoDTO);

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

