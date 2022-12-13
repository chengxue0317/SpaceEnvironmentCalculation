package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecBxyzMapper;
import cn.piesat.sec.model.dto.SecBxyzDTO;
import cn.piesat.sec.model.entity.SecBxyzDO;
import cn.piesat.sec.model.query.SecMagneticParamterQuery;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecMagneticParamterVO;
import cn.piesat.sec.service.SecBxyzService;
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
public class SecBxyzServiceImpl extends ServiceImpl<SecBxyzMapper, SecBxyzDO> implements SecBxyzService {
    Logger logger = LoggerFactory.getLogger(SecBxyzServiceImpl.class);

    @Autowired
    private SecBxyzMapper secBxyzMapper;

    @Override
    public SecEnvElementVO getBtxyzData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("太阳风磁场分量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<Object> dataY1 = new ArrayList<>();
            List<Object> dataY2 = new ArrayList<>();
            List<Object> dataY3 = new ArrayList<>();
            List<SecBxyzDO> list = secBxyzMapper.getBtxyzData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getBT());
                    dataY1.add(item.getBX());
                    dataY2.add(item.getBY());
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
        QueryWrapper<SecBxyzDO> wrapper = null;
        if (ObjectUtils.isEmpty(secMagneticParamterQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secMagneticParamterQuery);
        }
        IPage<SecBxyzDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecMagneticParamterVO::new));
    }

    @Override
    public SecMagneticParamterVO info(Serializable id) {
        SecBxyzDO secBxyzDO = getById(id);
        return CopyBean.copy(secBxyzDO,SecMagneticParamterVO::new);
    }
    @Override
    public Boolean save(SecBxyzDTO secMagneticParamterDTO) {
        SecBxyzDO secBxyzDO = CopyBean.copy(secMagneticParamterDTO, SecBxyzDO::new);
        return save(secBxyzDO);
    }

    @Override
    public Boolean update(SecBxyzDTO secMagneticParamterDTO) {
        SecBxyzDO secBxyzDO = this.getById(secMagneticParamterDTO.getId());
        BeanUtils.copyProperties(secMagneticParamterDTO, secBxyzDO);
        return updateById(secBxyzDO);
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
