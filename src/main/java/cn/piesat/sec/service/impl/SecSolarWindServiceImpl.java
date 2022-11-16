package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecSolarWindMapper;
import cn.piesat.sec.model.dto.SecSolarWindDTO;
import cn.piesat.sec.model.entity.SecSolarWindDO;
import cn.piesat.sec.model.query.SecSolarWindQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.model.vo.SecSolarWindVO;
import cn.piesat.sec.service.SecSolarWindService;
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
 * 太阳风速Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
@Service("secSolarWindService")
public class SecSolarWindServiceImpl extends ServiceImpl<SecSolarWindMapper, SecSolarWindDO> implements SecSolarWindService {

    Logger logger = LoggerFactory.getLogger(SecSolarWindServiceImpl.class);

    @Autowired
    private SecSolarWindMapper secSolarWindMapper;

    @Override
    public EnvElementVO getSolarWindData(String startTime, String endTime) {
        EnvElementVO eeb = new EnvElementVO();
        eeb.setTitleText("太阳风速");
        try {
            List<SecSolarWindDO> list = secSolarWindMapper.getSolarWindData(startTime, endTime);
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getBulkspeed());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain solar wind speed anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public PageResult list(PageBean pageBean, SecSolarWindQuery secSolarWindQuery) {
        QueryWrapper<SecSolarWindDO> wrapper = null;
        if (ObjectUtils.isEmpty(secSolarWindQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secSolarWindQuery);
        }
        IPage<SecSolarWindDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecSolarWindVO::new));
    }

    @Override
    public SecSolarWindVO info(Serializable id) {
        SecSolarWindDO secSolarWindDO = getById(id);
        return CopyBean.copy(secSolarWindDO,SecSolarWindVO::new);
    }
    @Override
    public Boolean save(SecSolarWindDTO secSolarWindDTO) {
        SecSolarWindDO secSolarWindDO = CopyBean.copy(secSolarWindDTO, SecSolarWindDO::new);
        return save(secSolarWindDO);
    }

    @Override
    public Boolean update(SecSolarWindDTO secSolarWindDTO) {
        SecSolarWindDO secSolarWindDO = this.getById(secSolarWindDTO.getId());
        BeanUtils.copyProperties(secSolarWindDTO,secSolarWindDO);
        return updateById(secSolarWindDO);
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
