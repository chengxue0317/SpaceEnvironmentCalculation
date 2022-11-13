package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.exception.DdResponseType;
import cn.piesat.kjyy.common.mybatisplus.utils.Query ;
import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder  ;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean  ;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.piesat.sec.model.dto.SecAlarmForecastDTO;
import cn.piesat.sec.model.query.SecAlarmForecastQuery;
import cn.piesat.sec.model.vo.SecAlarmForecastVO;
import cn.piesat.sec.dao.mapper.SecAlarmForecastMapper;
import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import cn.piesat.sec.service.SecAlarmForecastService;
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
 * 未来三天警报预报表Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:48:52
 */
@Service("secAlarmForecastService")
public class SecAlarmForecastServiceImpl extends ServiceImpl<SecAlarmForecastMapper, SecAlarmForecastDO> implements SecAlarmForecastService {

    @Override
    public PageResult list(PageBean pageBean, SecAlarmForecastQuery secAlarmForecastQuery) {
        QueryWrapper<SecAlarmForecastDO> wrapper = null;
        if (ObjectUtils.isEmpty(secAlarmForecastQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secAlarmForecastQuery);
        }
        IPage<SecAlarmForecastDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecAlarmForecastVO::new));
    }

    @Override
    public SecAlarmForecastVO info(Serializable id) {
        SecAlarmForecastDO secAlarmForecastDO = getById(id);
        return CopyBean.copy(secAlarmForecastDO,SecAlarmForecastVO::new);
    }
    @Override
    public Boolean save(SecAlarmForecastDTO secAlarmForecastDTO) {
        SecAlarmForecastDO secAlarmForecastDO = CopyBean.copy(secAlarmForecastDTO, SecAlarmForecastDO::new);
        return save(secAlarmForecastDO);
    }

    @Override
    public Boolean update(SecAlarmForecastDTO secAlarmForecastDTO) {
        SecAlarmForecastDO secAlarmForecastDO = this.getById(secAlarmForecastDTO.getId());
        BeanUtils.copyProperties(secAlarmForecastDTO,secAlarmForecastDO);
        return updateById(secAlarmForecastDO);
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
