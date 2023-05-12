package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecXrayFluxMapper;
import cn.piesat.sec.model.entity.SecXrayFluxDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecXrayFluxService;
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
    public SecEnvElementVO getSolarXrayData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
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
}
