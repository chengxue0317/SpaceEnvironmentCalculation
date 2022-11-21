package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecKpIndexDTO;
import cn.piesat.sec.model.query.SecKpIndexQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecKpIndexDO;
import cn.piesat.sec.model.vo.SecKpIndexVO;

import java.io.Serializable;
import java.util.List;

/**
 * KP指数Service接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
public interface SecKpIndexService extends IService<SecKpIndexDO> {
    /**
     * 获取一段时间范围内的AP指数数据
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return AP指数数据
     */
    SecEnvElementVO getKpData(String startTime, String endTime);

    /**
     * 分页查询
     *
     * @param pageBean        {@link PageBean} 分页对象
     * @param secKpIndexQuery {@link SecKpIndexQuery} KP指数查询对象
     * @return {@link PageResult} 查询结果
     */
    PageResult list(PageBean pageBean, SecKpIndexQuery secKpIndexQuery);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecKpIndexVO}
     */
    SecKpIndexVO info(Serializable id);

    /**
     * 新增
     *
     * @param secKpIndexDTO {@link SecKpIndexDTO} KP指数DTO
     * @return false or true
     */
    Boolean save(SecKpIndexDTO secKpIndexDTO);

    /**
     * 修改
     *
     * @param secKpIndexDTO {@link SecKpIndexDTO} KP指数DTO
     * @return false or true
     */
    Boolean update(SecKpIndexDTO secKpIndexDTO);

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

