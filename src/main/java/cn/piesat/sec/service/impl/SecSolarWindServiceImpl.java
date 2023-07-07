package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecSolarWindMapper;
import cn.piesat.sec.model.entity.SecSolarWindDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecSolarWindService;
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
    public SecEnvElementVO getSolarWindData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
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
}
