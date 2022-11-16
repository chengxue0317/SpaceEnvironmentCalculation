package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecParticleFluxMapper;
import cn.piesat.sec.model.dto.SecParticleFluxDTO;
import cn.piesat.sec.model.entity.SecParticleFluxDO;
import cn.piesat.sec.model.query.SecParticleFluxQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.model.vo.SecParticleFluxVO;
import cn.piesat.sec.service.SecParticleFluxService;
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
 * 高能粒子通量数据Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
@Service("secParticleFluxService")
public class SecParticleFluxServiceImpl extends ServiceImpl<SecParticleFluxMapper, SecParticleFluxDO> implements SecParticleFluxService {
    Logger logger = LoggerFactory.getLogger(SecParticleFluxServiceImpl.class);

    @Autowired
    private SecParticleFluxMapper secParticleFluxMapper;

    @Override
    public EnvElementVO getProtonFluxData(String startTime, String endTime, String satId) {
        EnvElementVO eeb = new EnvElementVO();
        eeb.setTitleText("质子通量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<Object> dataY1 = new ArrayList<>();
            List<Object> dataY2 = new ArrayList<>();
            List<Object> dataY3 = new ArrayList<>();
            List<Object> dataY4 = new ArrayList<>();
            List<Object> dataY5 = new ArrayList<>();
            List<SecParticleFluxDO> list = secParticleFluxMapper.getProtonFluxData(startTime, endTime, satId);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getP1());
                    dataY1.add(item.getP2());
                    dataY2.add(item.getP3());
                    dataY3.add(item.getP4());
                    dataY4.add(item.getP5());
                    dataY5.add(item.getP6());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
                eeb.setDataY1(dataY1);
                eeb.setDataY2(dataY2);
                eeb.setDataY3(dataY3);
                eeb.setDataY4(dataY4);
                eeb.setDataY5(dataY5);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get proton index anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public EnvElementVO getElectronicFluxData(String startTime, String endTime, String satId) {
        EnvElementVO eeb = new EnvElementVO();
        eeb.setTitleText("电子通量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecParticleFluxDO> list = secParticleFluxMapper.getElectronicFluxData(startTime, endTime, satId);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getE2());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get electronic index anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public PageResult list(PageBean pageBean, SecParticleFluxQuery secParticleFluxQuery) {
        QueryWrapper<SecParticleFluxDO> wrapper = null;
        if (ObjectUtils.isEmpty(secParticleFluxQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secParticleFluxQuery);
        }
        IPage<SecParticleFluxDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecParticleFluxVO::new));
    }

    @Override
    public SecParticleFluxVO info(Serializable id) {
        SecParticleFluxDO secParticleFluxDO = getById(id);
        return CopyBean.copy(secParticleFluxDO, SecParticleFluxVO::new);
    }

    @Override
    public Boolean save(SecParticleFluxDTO secParticleFluxDTO) {
        SecParticleFluxDO secParticleFluxDO = CopyBean.copy(secParticleFluxDTO, SecParticleFluxDO::new);
        return save(secParticleFluxDO);
    }

    @Override
    public Boolean update(SecParticleFluxDTO secParticleFluxDTO) {
        SecParticleFluxDO secParticleFluxDO = this.getById(secParticleFluxDTO.getId());
        BeanUtils.copyProperties(secParticleFluxDTO, secParticleFluxDO);
        return updateById(secParticleFluxDO);
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
