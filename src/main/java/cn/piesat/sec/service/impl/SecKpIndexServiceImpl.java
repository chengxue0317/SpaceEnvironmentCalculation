package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecKpIndexMapper;
import cn.piesat.sec.model.entity.SecKpIndexDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecKpIndexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * KP指数Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 16:57:26
 */
@Service("secKpIndexService")
public class SecKpIndexServiceImpl extends ServiceImpl<SecKpIndexMapper, SecKpIndexDO> implements SecKpIndexService {
    Logger logger = LoggerFactory.getLogger(SecKpIndexServiceImpl.class);

    @Autowired
    private SecKpIndexMapper secKpIndexMapper;

    @Override
    public SecEnvElementVO getKpData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("KP指数");
        try {
            List<SecKpIndexDO> list = secKpIndexMapper.getKPData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                splitKpData(eeb, list);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Obtain kp anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    private void splitKpData(SecEnvElementVO eeb, List<SecKpIndexDO> list) {
        List<Object> dataX = new ArrayList<>();
        List<Object> dataY = new ArrayList<>();
        list.forEach(item -> {
            LocalDateTime time = item.getTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            String ymd = time.format(formatter);
            dataX.add(ymd);
            dataY.add(item.getKp());
        });
        eeb.setDataX(dataX);
        eeb.setDataY(dataY);
    }
}
