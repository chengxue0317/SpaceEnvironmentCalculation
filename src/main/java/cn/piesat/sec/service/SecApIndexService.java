package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecApIndexDTO;
import cn.piesat.sec.model.query.SecApIndexQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecApIndexDO;
import cn.piesat.sec.model.vo.SecApIndexVO;

import java.io.Serializable;
import java.util.List;

/**
 * AP指数Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 15:53:01
 */
public interface SecApIndexService extends IService<SecApIndexDO> {
    /**
     * 获取一段时间范围内的AP指数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return AP指数数据
     */
    SecEnvElementVO getApData(String startTime, String endTime);

    /**
     * 分页查询
     *
     * @param pageBean        {@link PageBean} 分页对象
     * @param secApIndexQuery {@link SecApIndexQuery} AP指数查询对象
     * @return {@link PageResult} 查询结果
     */
    PageResult list(PageBean pageBean, SecApIndexQuery secApIndexQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecApIndexVO}
     */
    SecApIndexVO info(Serializable id);

    /**
     * 新增
     *
     * @param secApIndexDTO {@link SecApIndexDTO} AP指数DTO
     * @return false or true
     */
    Boolean save(SecApIndexDTO secApIndexDTO);

    /**
     * 修改
     *
     * @param secApIndexDTO {@link SecApIndexDTO} AP指数DTO
     * @return false or true
     */
    Boolean update(SecApIndexDTO secApIndexDTO);

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

