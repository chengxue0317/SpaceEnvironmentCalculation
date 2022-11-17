package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.exception.DdResponseType;
import cn.piesat.kjyy.common.mybatisplus.utils.Query ;
import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder  ;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean  ;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.piesat.sec.model.dto.SecAtmosphereDensityDTO;
import cn.piesat.sec.model.query.SecAtmosphereDensityQuery;
import cn.piesat.sec.model.vo.SecAtmosphereDensityVO;
import cn.piesat.sec.dao.mapper.SecAtmosphereDensityMapper;
import cn.piesat.sec.model.entity.SecAtmosphereDensityDO;
import cn.piesat.sec.service.SecAtmosphereDensityService;
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
 * 大气密度预报模块Service实现类
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-16 14:58:35
 */
@Service("secAtmosphereDensityService")
public class SecAtmosphereDensityServiceImpl extends ServiceImpl<SecAtmosphereDensityMapper, SecAtmosphereDensityDO> implements SecAtmosphereDensityService {

    @Override
    public PageResult list(PageBean pageBean, SecAtmosphereDensityQuery secAtmosphereDensityQuery) {
        QueryWrapper<SecAtmosphereDensityDO> wrapper = null;
        if (ObjectUtils.isEmpty(secAtmosphereDensityQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secAtmosphereDensityQuery);
        }
        IPage<SecAtmosphereDensityDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecAtmosphereDensityVO::new));
    }

    @Override
    public SecAtmosphereDensityVO info(Serializable id) {
        SecAtmosphereDensityDO secAtmosphereDensityDO = getById(id);
        return CopyBean.copy(secAtmosphereDensityDO,SecAtmosphereDensityVO::new);
    }
    @Override
    public Boolean save(SecAtmosphereDensityDTO secAtmosphereDensityDTO) {
        SecAtmosphereDensityDO secAtmosphereDensityDO = CopyBean.copy(secAtmosphereDensityDTO, SecAtmosphereDensityDO::new);
        return save(secAtmosphereDensityDO);
    }

    @Override
    public Boolean update(SecAtmosphereDensityDTO secAtmosphereDensityDTO) {
        SecAtmosphereDensityDO secAtmosphereDensityDO = this.getById(secAtmosphereDensityDTO.getId());
        BeanUtils.copyProperties(secAtmosphereDensityDTO,secAtmosphereDensityDO);
        return updateById(secAtmosphereDensityDO);
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
