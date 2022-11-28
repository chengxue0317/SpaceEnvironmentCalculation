package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecStaInfoMapper;
import cn.piesat.sec.model.dto.SecStaInfoDTO;
import cn.piesat.sec.model.entity.SecStaInfoDO;
import cn.piesat.sec.model.query.SecStaInfoQuery;
import cn.piesat.sec.model.vo.SecStaInfoVO;
import cn.piesat.sec.service.SecStaInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 台站信息表Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-27 10:51:47
 */
@Service("secStaInfoService")
public class SecStaInfoServiceImpl extends ServiceImpl<SecStaInfoMapper, SecStaInfoDO> implements SecStaInfoService {

    @Override
    public PageResult list(PageBean pageBean, SecStaInfoQuery secStaInfoQuery) {
        QueryWrapper<SecStaInfoDO> wrapper = null;
        if (ObjectUtils.isEmpty(secStaInfoQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secStaInfoQuery);
        }
        IPage<SecStaInfoDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecStaInfoVO::new));
    }

    @Override
    public SecStaInfoVO info(Serializable id) {
        SecStaInfoDO secStaInfoDO = getById(id);
        return CopyBean.copy(secStaInfoDO,SecStaInfoVO::new);
    }
    @Override
    public Boolean save(SecStaInfoDTO secStaInfoDTO) {
        SecStaInfoDO secStaInfoDO = CopyBean.copy(secStaInfoDTO, SecStaInfoDO::new);
        return save(secStaInfoDO);
    }

    @Override
    public Boolean update(SecStaInfoDTO secStaInfoDTO) {
        SecStaInfoDO secStaInfoDO = this.getById(secStaInfoDTO.getId());
        BeanUtils.copyProperties(secStaInfoDTO,secStaInfoDO);
        return updateById(secStaInfoDO);
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
