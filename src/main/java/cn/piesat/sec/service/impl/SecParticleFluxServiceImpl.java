package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecParticleFluxMapper;
import cn.piesat.sec.model.entity.SecParticleFluxDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.service.SecParticleFluxService;
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
 * 高能粒子通量数据Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-15 11:11:30
 */
@Service("secParticleFluxService")
public class SecParticleFluxServiceImpl extends ServiceImpl<SecParticleFluxMapper, SecParticleFluxDO> implements SecParticleFluxService {
    Logger logger = LoggerFactory.getLogger(SecParticleFluxServiceImpl.class);

    @Autowired
    private SecParticleFluxMapper secParticleFluxMapper;

    @Override
    public SecEnvElementVO getProtonFluxData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("质子通量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<Object> dataY1 = new ArrayList<>();
            List<Object> dataY2 = new ArrayList<>();
            List<Object> dataY3 = new ArrayList<>();
            List<Object> dataY4 = new ArrayList<>();
            List<Object> dataY5 = new ArrayList<>();
            List<SecParticleFluxDO> list = secParticleFluxMapper.getProtonFluxData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getP1());
                    dataY1.add(item.getP2());
                    dataY2.add(item.getP3());
                    dataY3.add(item.getP4());
                    dataY4.add(item.getP5());
                    dataY5.add(item.getP6());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
                eeb.setDataY1(dataY1);
                eeb.setDataY2(dataY2);
                eeb.setDataY3(dataY3);
                eeb.setDataY4(dataY4);
                eeb.setDataY5(dataY5);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get proton index anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public SecEnvElementVO getElectronicFluxData(String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("电子通量");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<SecParticleFluxDO> list = secParticleFluxMapper.getElectronicFluxData(startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    dataX.add(item.getTime());
                    dataY.add(item.getE2());
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get electronic index anomaly %s", e.getMessage()));
        }
        return eeb;
    }
}
