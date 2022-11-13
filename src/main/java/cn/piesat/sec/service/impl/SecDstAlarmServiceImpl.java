package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.exception.DdResponseType;
import cn.piesat.kjyy.common.mybatisplus.utils.Query ;
import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder  ;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean  ;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.piesat.sec.model.dto.SecDstAlarmDTO;
import cn.piesat.sec.model.query.SecDstAlarmQuery;
import cn.piesat.sec.model.vo.SecDstAlarmVO;
import cn.piesat.sec.dao.mapper.SecDstAlarmMapper;
import cn.piesat.sec.model.entity.SecDstAlarmDO;
import cn.piesat.sec.service.SecDstAlarmService;
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
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-12 14:47:57
 */
@Service("secDstAlarmService")
public class SecDstAlarmServiceImpl extends ServiceImpl<SecDstAlarmMapper, SecDstAlarmDO> implements SecDstAlarmService {

    @Override
    public PageResult list(PageBean pageBean, SecDstAlarmQuery secDstAlarmQuery) {
        QueryWrapper<SecDstAlarmDO> wrapper = null;
        if (ObjectUtils.isEmpty(secDstAlarmQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secDstAlarmQuery);
        }
        IPage<SecDstAlarmDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecDstAlarmVO::new));
    }

    @Override
    public SecDstAlarmVO info(Serializable id) {
        SecDstAlarmDO secDstAlarmDO = getById(id);
        return CopyBean.copy(secDstAlarmDO,SecDstAlarmVO::new);
    }
    @Override
    public Boolean save(SecDstAlarmDTO secDstAlarmDTO) {
        SecDstAlarmDO secDstAlarmDO = CopyBean.copy(secDstAlarmDTO, SecDstAlarmDO::new);
        return save(secDstAlarmDO);
    }

    @Override
    public Boolean update(SecDstAlarmDTO secDstAlarmDTO) {
        SecDstAlarmDO secDstAlarmDO = this.getById(secDstAlarmDTO.getId());
        BeanUtils.copyProperties(secDstAlarmDTO,secDstAlarmDO);
        return updateById(secDstAlarmDO);
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
