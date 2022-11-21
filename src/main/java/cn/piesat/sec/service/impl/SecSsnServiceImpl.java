package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecSsnMapper;
import cn.piesat.sec.model.dto.SecSsnDTO;
import cn.piesat.sec.model.entity.SecSsnDO;
import cn.piesat.sec.model.query.SecSsnQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecSsnVO;
import cn.piesat.sec.service.SecSsnService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 太阳黑子数Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Service("secSsnService")
public class SecSsnServiceImpl extends ServiceImpl<SecSsnMapper, SecSsnDO> implements SecSsnService {
    Logger logger = LoggerFactory.getLogger(SecSsnServiceImpl.class);

    @Autowired
    SecSsnMapper SecSsnMapper;

    @Override
    public SecEnvElementVO getSunSpotData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("太阳黑子数");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecSsnDO> list = SecSsnMapper.getSunSpotData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getSsn());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain sunspot anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public PageResult list(PageBean pageBean, SecSsnQuery secSsnQuery) {
        QueryWrapper<SecSsnDO> wrapper = null;
        if (ObjectUtils.isEmpty(secSsnQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secSsnQuery);
        }
        IPage<SecSsnDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecSsnVO::new));
    }

    @Override
    public SecSsnVO info(Serializable id) {
        SecSsnDO secSsnDO = getById(id);
        return CopyBean.copy(secSsnDO, SecSsnVO::new);
    }

    @Override
    public Boolean save(SecSsnDTO secSsnDTO) {
        SecSsnDO secSsnDO = CopyBean.copy(secSsnDTO, SecSsnDO::new);
        return save(secSsnDO);
    }

    @Override
    public Boolean update(SecSsnDTO secSsnDTO) {
        SecSsnDO secSsnDO = this.getById(secSsnDTO.getId());
        BeanUtils.copyProperties(secSsnDTO, secSsnDO);
        return updateById(secSsnDO);
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
