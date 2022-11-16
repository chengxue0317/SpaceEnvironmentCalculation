package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SecApIndexMapper;
import cn.piesat.sec.model.dto.SecApIndexDTO;
import cn.piesat.sec.model.entity.SecApIndexDO;
import cn.piesat.sec.model.query.SecApIndexQuery;
import cn.piesat.sec.model.vo.EnvElementVO;
import cn.piesat.sec.model.vo.SecApIndexVO;
import cn.piesat.sec.service.SecApIndexService;
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
 * AP指数Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 15:53:01
 */
@Service("secApIndexService")
public class SecApIndexServiceImpl extends ServiceImpl<SecApIndexMapper, SecApIndexDO> implements SecApIndexService {
    Logger logger = LoggerFactory.getLogger(SecApIndexServiceImpl.class);

    @Autowired
    private SecApIndexMapper secApIndexMapper;

    @Override
    public EnvElementVO getApData(String startTime, String endTime) {
        EnvElementVO eeb = new EnvElementVO();
        eeb.setTitleText("AP指数");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecApIndexDO> list = secApIndexMapper.getAPData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getAp());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain ap anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public PageResult list(PageBean pageBean, SecApIndexQuery secApIndexQuery) {
        QueryWrapper<SecApIndexDO> wrapper = null;
        if (ObjectUtils.isEmpty(secApIndexQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(secApIndexQuery);
        }
        IPage<SecApIndexDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SecApIndexVO::new));
    }

    @Override
    public SecApIndexVO info(Serializable id) {
        SecApIndexDO secApIndexDO = getById(id);
        return CopyBean.copy(secApIndexDO,SecApIndexVO::new);
    }
    @Override
    public Boolean save(SecApIndexDTO secApIndexDTO) {
        SecApIndexDO secApIndexDO = CopyBean.copy(secApIndexDTO, SecApIndexDO::new);
        return save(secApIndexDO);
    }

    @Override
    public Boolean update(SecApIndexDTO secApIndexDTO) {
        SecApIndexDO secApIndexDO = this.getById(secApIndexDTO.getId());
        BeanUtils.copyProperties(secApIndexDTO,secApIndexDO);
        return updateById(secApIndexDO);
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
