package cn.piesat.sec.service;

import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.dto.SecShieldose2DTO;
import cn.piesat.sec.model.query.SecShieldose2Query;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.SecShieldose2DO;
import cn.piesat.sec.model.vo.SecShieldose2VO;

import java.io.Serializable;
import java.util.List;

/**
 * ${comments}Service接口
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-12-02 13:24:52
 */
public interface SecShieldose2Service extends IService<SecShieldose2DO> {

    /**
     * 分页查询
     *
     * @param pageBean {@link PageBean} 分页对象
     * @param secShieldose2Query {@link SecShieldose2Query} ${comments}查询对象
     * @return {@link PageResult} 查询结果
    */
    PageResult list(PageBean pageBean, SecShieldose2Query secShieldose2Query);

    /**
     * 根据id查询
     *
     * @param id id
     * @return {@link SecShieldose2VO}
    */
    SecShieldose2VO info(Serializable id);

    /**
     * 新增
     *
     * @param secShieldose2DTO {@link SecShieldose2DTO} ${comments}DTO
     * @return false or true
    */
    Boolean save(SecShieldose2DTO secShieldose2DTO);

    /**
     * 修改
     *
     * @param secShieldose2DTO {@link SecShieldose2DTO} ${comments}DTO
     * @return false or true
     */
    Boolean update(SecShieldose2DTO secShieldose2DTO);

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

