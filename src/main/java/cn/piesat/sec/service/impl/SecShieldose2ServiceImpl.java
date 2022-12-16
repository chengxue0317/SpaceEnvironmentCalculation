package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.exception.DdResponseType;
import cn.piesat.kjyy.common.mybatisplus.utils.Query ;
import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder  ;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean  ;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.piesat.sec.model.dto.SecShieldose2DTO;
import cn.piesat.sec.model.query.SecShieldose2Query;
import cn.piesat.sec.model.vo.SecShieldose2VO;
import cn.piesat.sec.dao.mapper.SecShieldose2Mapper;
import cn.piesat.sec.model.entity.SecShieldose2DO;
import cn.piesat.sec.service.SecShieldose2Service;
import cn.piesat.sec.exception.SecException;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import java.io.Serializable;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.ObjectUtils;

/**
 * ${comments}Service实现类
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-12-02 13:24:52
 */
@Service("secShieldose2Service")
public class SecShieldose2ServiceImpl extends ServiceImpl<SecShieldose2Mapper, SecShieldose2DO> implements SecShieldose2Service {

    @Override
    public PageResult list(PageBean pageBean, SecShieldose2Query secShieldose2Query) {
        QueryWrapper<SecShieldose2DO> wrapper = null;
        if (ObjectUtils.isEmpty(secShieldose2Query)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secShieldose2Query);
        }
        IPage<SecShieldose2DO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecShieldose2VO::new));
    }

    @Override
    public SecShieldose2VO info(Serializable id) {
        SecShieldose2DO secShieldose2DO = getById(id);
        return CopyBean.copy(secShieldose2DO,SecShieldose2VO::new);
    }
    @Override
    public Boolean save(SecShieldose2DTO secShieldose2DTO) {
        SecShieldose2DO secShieldose2DO = CopyBean.copy(secShieldose2DTO, SecShieldose2DO::new);
        return save(secShieldose2DO);
    }

    @Override
    public Boolean update(SecShieldose2DTO secShieldose2DTO) {
        SecShieldose2DO secShieldose2DO = this.getById(secShieldose2DTO.getId());
        BeanUtils.copyProperties(secShieldose2DTO,secShieldose2DO);
        return updateById(secShieldose2DO);
    }

    @Override
    public Boolean delete(List<Serializable> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    public Boolean delete(Serializable id) {
        return removeById(id);
    }
}
