package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecXrayAlarmMapper;
import cn.piesat.sec.model.dto.SecXrayAlarmDTO;
import cn.piesat.sec.model.entity.SecXrayAlarmDO;
import cn.piesat.sec.model.query.SecXrayAlarmQuery;
import cn.piesat.sec.model.vo.SecXrayAlarmVO;
import cn.piesat.sec.service.SecXrayAlarmService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳X射线耀斑警报事件Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:46:40
 */
@Service("secXrayAlarmService")
public class SecXrayAlarmServiceImpl extends ServiceImpl<SecXrayAlarmMapper, SecXrayAlarmDO> implements SecXrayAlarmService {

    @Override
    public PageResult list(PageBean pageBean, SecXrayAlarmQuery secXrayAlarmQuery) {
        QueryWrapper<SecXrayAlarmDO> wrapper = null;
        if (ObjectUtils.isEmpty(secXrayAlarmQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secXrayAlarmQuery);
        }
        IPage<SecXrayAlarmDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecXrayAlarmVO::new));
    }

    @Override
    public SecXrayAlarmVO info(Serializable id) {
        SecXrayAlarmDO secXrayAlarmDO = getById(id);
        return CopyBean.copy(secXrayAlarmDO,SecXrayAlarmVO::new);
    }
    @Override
    public Boolean save(SecXrayAlarmDTO secXrayAlarmDTO) {
        SecXrayAlarmDO secXrayAlarmDO = CopyBean.copy(secXrayAlarmDTO, SecXrayAlarmDO::new);
        return save(secXrayAlarmDO);
    }

    @Override
    public Boolean update(SecXrayAlarmDTO secXrayAlarmDTO) {
        SecXrayAlarmDO secXrayAlarmDO = this.getById(secXrayAlarmDTO.getId());
        BeanUtils.copyProperties(secXrayAlarmDTO,secXrayAlarmDO);
        return updateById(secXrayAlarmDO);
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
