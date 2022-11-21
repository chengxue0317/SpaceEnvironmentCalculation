package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.dao.mapper.SecDstIndexMapper;
import cn.piesat.sec.model.entity.SecDstIndexDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecDstIndexService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service("secDstIndexService")
public class SecDstIndexServiceImpl extends ServiceImpl<SecDstIndexMapper, SecDstIndexDO> implements SecDstIndexService {
    Logger logger = LoggerFactory.getLogger(SecDstIndexServiceImpl.class);

    @Autowired
    private SecDstIndexMapper secDstIndexMapper;

    @Override
    public SecEnvElementVO getDstData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("DST");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecDstIndexDO> list = secDstIndexMapper.getDSTData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getDst());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain dst anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public PageResult list(PageBean pageBean, SecDstIndexDO secDstIndexDO) {
        QueryWrapper<SecDstIndexDO> wrapper = new QueryWrapper<>(secDstIndexDO);
        IPage<SecDstIndexDO> page = this.page(
                Query.getPage(pageBean),
                wrapper
        );
        return new PageResult(page.getTotal(), page.getRecords());
    }

    @Override
    public SecDstIndexDO info(Long id) {
        return getById(id);
    }
    @Override
    public Boolean add(SecDstIndexDO secDstIndexDO) {
        return save(secDstIndexDO);
    }

    @Override
    public Boolean update(SecDstIndexDO secDstIndexDO) {
        return updateById(secDstIndexDO) ;
    }

    @Override
    public Boolean delete(List<Long> asList) {
        return removeBatchByIds(asList);
    }

    @Override
    public Boolean delete(Long id) {
        return removeById(id);
    }
}
