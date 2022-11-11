package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.ProtonAlarmMapper;
import cn.piesat.sec.model.dto.ProtonAlarmDTO;
import cn.piesat.sec.model.entity.AlarmEventDO;
import cn.piesat.sec.model.query.ProtonAlarmQuery;
import cn.piesat.sec.model.vo.ProtonAlarmVO;
import cn.piesat.sec.service.ProtonAlarmService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 太阳质子事件Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-08 23:02:08
 */
@Service("protonAlarmService")
public class ProtonAlarmServiceImpl extends ServiceImpl<ProtonAlarmMapper, AlarmEventDO> implements ProtonAlarmService {

    @Override
    public PageResult list(PageBean pageBean, ProtonAlarmQuery protonAlarmQuery) {
        QueryWrapper<AlarmEventDO> wrapper = null;
        if (ObjectUtils.isEmpty(protonAlarmQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(protonAlarmQuery);
        }
        IPage<AlarmEventDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), ProtonAlarmVO::new));
    }

    @Override
    public ProtonAlarmVO info(Serializable id) {
        AlarmEventDO protonAlarmDO = getById(id);
        return CopyBean.copy(protonAlarmDO, ProtonAlarmVO::new);
    }

    @Override
    public Boolean save(ProtonAlarmDTO protonAlarmDTO) {
        AlarmEventDO protonAlarmDO = CopyBean.copy(protonAlarmDTO, AlarmEventDO::new);
        return save(protonAlarmDO);
    }

    @Override
    public Boolean update(ProtonAlarmDTO protonAlarmDTO) {
        AlarmEventDO protonAlarmDO = this.getById(protonAlarmDTO.getId());
        BeanUtils.copyProperties(protonAlarmDTO, protonAlarmDO);
        return updateById(protonAlarmDO);
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
