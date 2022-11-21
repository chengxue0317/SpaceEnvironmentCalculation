package cn.piesat.sec.service.impl;

import cn.piesat.sec.dao.mapper.SecEnvOverviewMapper;
import cn.piesat.sec.model.vo.SecEnvOverviewVO;
import cn.piesat.sec.service.SecEnvOverviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service("secEnvOverviewService")
public class SecEnvOverviewServiceImpl implements SecEnvOverviewService {
    private static final Logger logger = LoggerFactory.getLogger(SecEnvOverviewServiceImpl.class);

    @Autowired
    private SecEnvOverviewMapper secEnvOverviewMapper;

    @Override
    public List<SecEnvOverviewVO> getEnvOverview() {
        List<SecEnvOverviewVO> res = null;
        try {
           res = secEnvOverviewMapper.getEnvOverview();
        } catch (Exception e) {
            res = new ArrayList<>();
            logger.error(String.format(Locale.ROOT, String.format("---The space environment forecast information fails to be obtained %s-", e.getMessage())));
        } finally {
            return res;
        }
    }
}
