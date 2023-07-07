package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecSsnMapper;
import cn.piesat.sec.model.entity.SecSsnDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecSsnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 太阳黑子数Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-13 20:02:03
 */
@Service("secSsnService")
public class SecSsnServiceImpl extends ServiceImpl<SecSsnMapper, SecSsnDO> implements SecSsnService {
    Logger logger = LoggerFactory.getLogger(SecSsnServiceImpl.class);

    @Autowired
    SecSsnMapper SecSsnMapper;

    @Override
    public SecEnvElementVO getSunSpotData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("太阳黑子数");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecSsnDO> list = SecSsnMapper.getSunSpotData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getSsn());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain sunspot anomaly %s", e.getMessage()));
        }
        return eeb;
    }
}
