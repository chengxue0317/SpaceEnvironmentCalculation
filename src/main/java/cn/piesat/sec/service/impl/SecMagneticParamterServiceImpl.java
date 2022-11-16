package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecMagneticParamterMapper;
import cn.piesat.sec.model.dto.SecMagneticParamterDTO;
import cn.piesat.sec.model.entity.SecMagneticParamterDO;
import cn.piesat.sec.model.query.SecMagneticParamterQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.model.vo.SecMagneticParamterVO;
import cn.piesat.sec.service.SecMagneticParamterService;
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
 * 地磁参数数据Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:52:42
 */
@Service("secMagneticParamterService")
public class SecMagneticParamterServiceImpl extends ServiceImpl<SecMagneticParamterMapper, SecMagneticParamterDO> implements SecMagneticParamterService {
    Logger logger = LoggerFactory.getLogger(SecMagneticParamterServiceImpl.class);

    @Autowired
    private SecMagneticParamterMapper secMagneticParamterMapper;

    @Override
    public EnvElementVO getBtxyzData(String startTime, String endTime) {
        EnvElementVO eeb = new EnvElementVO();
        eeb.setTitleText("太阳风磁场分量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<Object> dataY1 = new ArrayList<>();
            List<Object> dataY2 = new ArrayList<>();
            List<Object> dataY3 = new ArrayList<>();
            List<SecMagneticParamterDO> list = secMagneticParamterMapper.getBtxyzData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    Double bh = item.getBH();
                    Double bd = item.getBD();
                    dataX.add(item.getTime());
                    dataY.add(item.getBT());
                    dataY1.add(bh * Math.cos(bd));
                    dataY2.add(bh * Math.sin(bd));
                    dataY3.add(item.getBZ());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
                eeb.setDataY1(dataY1);
                eeb.setDataY2(dataY2);
                eeb.setDataY3(dataY3);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get magnetic parameter anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public PageResult list(PageBean pageBean, SecMagneticParamterQuery secMagneticParamterQuery) {
        QueryWrapper<SecMagneticParamterDO> wrapper = null;
        if (ObjectUtils.isEmpty(secMagneticParamterQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secMagneticParamterQuery);
        }
        IPage<SecMagneticParamterDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecMagneticParamterVO::new));
    }

    @Override
    public SecMagneticParamterVO info(Serializable id) {
        SecMagneticParamterDO secMagneticParamterDO = getById(id);
        return CopyBean.copy(secMagneticParamterDO,SecMagneticParamterVO::new);
    }
    @Override
    public Boolean save(SecMagneticParamterDTO secMagneticParamterDTO) {
        SecMagneticParamterDO secMagneticParamterDO = CopyBean.copy(secMagneticParamterDTO, SecMagneticParamterDO::new);
        return save(secMagneticParamterDO);
    }

    @Override
    public Boolean update(SecMagneticParamterDTO secMagneticParamterDTO) {
        SecMagneticParamterDO secMagneticParamterDO = this.getById(secMagneticParamterDTO.getId());
        BeanUtils.copyProperties(secMagneticParamterDTO,secMagneticParamterDO);
        return updateById(secMagneticParamterDO);
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
