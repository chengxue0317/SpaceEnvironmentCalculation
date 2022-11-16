package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecXrayFluxMapper;
import cn.piesat.sec.model.dto.SecXrayFluxDTO;
import cn.piesat.sec.model.entity.SecXrayFluxDO;
import cn.piesat.sec.model.query.SecXrayFluxQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.model.vo.SecXrayFluxVO;
import cn.piesat.sec.service.SecXrayFluxService;
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
 * 太阳X射线流量Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 12:16:14
 */
@Service("secXrayFluxService")
public class SecXrayFluxServiceImpl extends ServiceImpl<SecXrayFluxMapper, SecXrayFluxDO> implements SecXrayFluxService {
    Logger logger = LoggerFactory.getLogger(SecXrayFluxServiceImpl.class);

    @Autowired
    SecXrayFluxMapper secXrayFluxMapper;

    @Override
    public EnvElementVO getSolarXrayData(String startTime, String endTime) {
        EnvElementVO eeb = new EnvElementVO();
        eeb.setTitleText("太阳X射线耀斑");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<Object> dataY1 = new ArrayList<>();
            List<SecXrayFluxDO> list = secXrayFluxMapper.getSolarXrayData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getLonger());
                    dataY1.add(item.getShorter());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
                eeb.setDataY1(dataY1);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get xray data anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public PageResult list(PageBean pageBean, SecXrayFluxQuery secXrayFluxQuery) {
        QueryWrapper<SecXrayFluxDO> wrapper = null;
        if (ObjectUtils.isEmpty(secXrayFluxQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secXrayFluxQuery);
        }
        IPage<SecXrayFluxDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecXrayFluxVO::new));
    }

    @Override
    public SecXrayFluxVO info(Serializable id) {
        SecXrayFluxDO secXrayFluxDO = getById(id);
        return CopyBean.copy(secXrayFluxDO,SecXrayFluxVO::new);
    }
    @Override
    public Boolean save(SecXrayFluxDTO secXrayFluxDTO) {
        SecXrayFluxDO secXrayFluxDO = CopyBean.copy(secXrayFluxDTO, SecXrayFluxDO::new);
        return save(secXrayFluxDO);
    }

    @Override
    public Boolean update(SecXrayFluxDTO secXrayFluxDTO) {
        SecXrayFluxDO secXrayFluxDO = this.getById(secXrayFluxDTO.getId());
        BeanUtils.copyProperties(secXrayFluxDTO,secXrayFluxDO);
        return updateById(secXrayFluxDO);
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
