package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecF107FluxMapper;
import cn.piesat.sec.model.entity.SecF107FluxDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecF107FluxService;
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
 * 太阳F10.7指数Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 20:49:13
 */
@Service("secF107FluxService")
public class SecF107FluxServiceImpl extends ServiceImpl<SecF107FluxMapper, SecF107FluxDO> implements SecF107FluxService {
    Logger logger = LoggerFactory.getLogger(SecDstIndexServiceImpl.class);

    @Autowired
    private SecF107FluxMapper secF107FluxMapper;

    @Override
    public SecEnvElementVO getF107Data(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("F10.7射电流量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecF107FluxDO> list = secF107FluxMapper.getF107Data(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getF107());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain f10.7 anomaly %s", e.getMessage()));
        }
        return eeb;
    }
}
