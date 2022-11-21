package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecF107FluxMapper;
import cn.piesat.sec.model.dto.SecF107FluxDTO;
import cn.piesat.sec.model.entity.SecF107FluxDO;
import cn.piesat.sec.model.query.SecF107FluxQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecF107FluxVO;
import cn.piesat.sec.service.SecF107FluxService;
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
 * 太阳F10.7指数Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
@Service("secF107FluxService")
public class SecF107FluxServiceImpl extends ServiceImpl<SecF107FluxMapper, SecF107FluxDO> implements SecF107FluxService {
    Logger logger = LoggerFactory.getLogger(SecDstIndexServiceImpl.class);

    @Autowired
    private SecF107FluxMapper secF107FluxMapper;

    @Override
    public SecEnvElementVO getF107Data(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("F10.7射电流量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecF107FluxDO> list = secF107FluxMapper.getF107Data(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getF107());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain f10.7 anomaly %s", e.getMessage()));
        }
        return eeb;
    }


    @Override
    public PageResult list(PageBean pageBean, SecF107FluxQuery secF107FluxQuery) {
        QueryWrapper<SecF107FluxDO> wrapper = null;
        if (ObjectUtils.isEmpty(secF107FluxQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secF107FluxQuery);
        }
        IPage<SecF107FluxDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecF107FluxVO::new));
    }

    @Override
    public SecF107FluxVO info(Serializable id) {
        SecF107FluxDO secF107FluxDO = getById(id);
        return CopyBean.copy(secF107FluxDO,SecF107FluxVO::new);
    }
    @Override
    public Boolean save(SecF107FluxDTO secF107FluxDTO) {
        SecF107FluxDO secF107FluxDO = CopyBean.copy(secF107FluxDTO, SecF107FluxDO::new);
        return save(secF107FluxDO);
    }

    @Override
    public Boolean update(SecF107FluxDTO secF107FluxDTO) {
        SecF107FluxDO secF107FluxDO = this.getById(secF107FluxDTO.getId());
        BeanUtils.copyProperties(secF107FluxDTO,secF107FluxDO);
        return updateById(secF107FluxDO);
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
