package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecSolarWindDTO;
import cn.piesat.sec.model.query.SecSolarWindQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecSolarWindDO;
import cn.piesat.sec.model.vo.SecSolarWindVO;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳风速Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
public interface SecSolarWindService extends IService<SecSolarWindDO> {
    /**
     * 获取一段时间范围内的太阳风速数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 太阳风速数据
     */
    SecEnvElementVO getSolarWindData(String startTime, String endTime);

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secSolarWindQuery {@link SecSolarWindQuery} 太阳风速查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecSolarWindQuery secSolarWindQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecSolarWindVO}
    */
    SecSolarWindVO info(Serializable id);

    /**
     * 新增
     *
     * @param secSolarWindDTO {@link SecSolarWindDTO} 太阳风速DTO
     * @return false or true
    */
    Boolean save(SecSolarWindDTO secSolarWindDTO);

    /**
     * 修改
     *
     * @param secSolarWindDTO {@link SecSolarWindDTO} 太阳风速DTO
     * @return false or true
     */
    Boolean update(SecSolarWindDTO secSolarWindDTO);

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

