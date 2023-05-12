package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecDstIndexMapper;
import cn.piesat.sec.model.entity.SecDstIndexDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecDstIndexService;
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
}
