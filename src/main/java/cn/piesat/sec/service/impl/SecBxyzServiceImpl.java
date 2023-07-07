package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecBxyzMapper;
import cn.piesat.sec.model.entity.SecBxyzDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecBxyzService;
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
}
