package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecKpIndexMapper;
import cn.piesat.sec.model.dto.SecKpIndexDTO;
import cn.piesat.sec.model.entity.SecKpIndexDO;
import cn.piesat.sec.model.query.SecKpIndexQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.model.vo.SecKpIndexVO;
import cn.piesat.sec.service.SecKpIndexService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * KP指数Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
@Service("secKpIndexService")
public class SecKpIndexServiceImpl extends ServiceImpl<SecKpIndexMapper, SecKpIndexDO> implements SecKpIndexService {
    Logger logger = LoggerFactory.getLogger(SecKpIndexServiceImpl.class);

    @Autowired
    private SecKpIndexMapper secKpIndexMapper;

    @Override
    public EnvElementVO getKpData(String startTime, String endTime) {
        EnvElementVO eeb = new EnvElementVO();
        eeb.setTitleText("KP指数");
        try {
            List<SecKpIndexDO> list = secKpIndexMapper.getKPData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                splitKpData(eeb, list);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain kp anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    private void splitKpData(EnvElementVO eeb, List<SecKpIndexDO> list) {
        List<Object> dataX = new ArrayList<>();
        List<Object> dataY = new ArrayList<>();
        list.forEach(item -> {
            LocalDateTime time = item.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String ymd = time.format(formatter);
            dataX.add(ymd.concat(" 00"));
            dataX.add(ymd.concat(" 03"));
            dataX.add(ymd.concat(" 06"));
            dataX.add(ymd.concat(" 09"));
            dataX.add(ymd.concat(" 12"));
            dataX.add(ymd.concat(" 15"));
            dataX.add(ymd.concat(" 18"));
            dataX.add(ymd.concat(" 21"));
            dataY.add(item.getKp1());
            dataY.add(item.getKp2());
            dataY.add(item.getKp3());
            dataY.add(item.getKp4());
            dataY.add(item.getKp5());
            dataY.add(item.getKp6());
            dataY.add(item.getKp7());
            dataY.add(item.getKp8());
        });
        eeb.setDataX(dataX);
        eeb.setDataY(dataY);
    }

    @Override
    public PageResult list(PageBean pageBean, SecKpIndexQuery secKpIndexQuery) {
        QueryWrapper<SecKpIndexDO> wrapper = null;
        if (ObjectUtils.isEmpty(secKpIndexQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secKpIndexQuery);
        }
        IPage<SecKpIndexDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecKpIndexVO::new));
    }

    @Override
    public SecKpIndexVO info(Serializable id) {
        SecKpIndexDO secKpIndexDO = getById(id);
        return CopyBean.copy(secKpIndexDO, SecKpIndexVO::new);
    }

    @Override
    public Boolean save(SecKpIndexDTO secKpIndexDTO) {
        SecKpIndexDO secKpIndexDO = CopyBean.copy(secKpIndexDTO, SecKpIndexDO::new);
        return save(secKpIndexDO);
    }

    @Override
    public Boolean update(SecKpIndexDTO secKpIndexDTO) {
        SecKpIndexDO secKpIndexDO = this.getById(secKpIndexDTO.getId());
        BeanUtils.copyProperties(secKpIndexDTO, secKpIndexDO);
        return updateById(secKpIndexDO);
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
