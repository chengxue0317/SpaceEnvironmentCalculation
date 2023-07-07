package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecApIndexMapper;
import cn.piesat.sec.model.entity.SecApIndexDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecApIndexService;
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
    public SecEnvElementVO getApData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
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
}
