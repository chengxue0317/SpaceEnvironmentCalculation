package cn.piesat.sec.service.impl.dataparse;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.dao.mapper.dataparse.MonthForeMapper;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.model.vo.dataparse.MonthForeVo;
import cn.piesat.sec.service.SecSpaceEnvData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("MonthForeService")
public class MonthForeImpl implements SecSpaceEnvData {
    private static final Logger logger = LoggerFactory.getLogger(SoF107Impl.class);
    @Autowired
    private MonthForeMapper monthForeMapper;

    @Override
    public int parseData(SecIISVO secIISVO) {
        // 下载文件
        String uuid = UUID.randomUUID().toString();
        OSSInstance.getOSSUtil().download(secIISVO.getBucketName(), secIISVO.getKey(), uuid);
        // 解析文件数据
        String filePath = File.separator.concat(uuid).concat(File.separator).concat(secIISVO.getKey());
        filePath = FileUtil.checkPath(filePath);
        List<String> content = FileUtil.readTxtFile2List(filePath);
        int dataNum = 0;
        List<MonthForeVo> objList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(content) && content.size() >= 2) {
            MonthForeVo vo = new MonthForeVo();
            vo.setTime(DateUtil.parseLocalDateTime(content.get(0), DateConstant.DATE_TIME_PATTERN2));
            content.remove(0);
            String monthView = StringUtils.join(content, "</br>");
            if (StringUtils.isNotEmpty(monthView)) {
                int nextIdx = monthView.indexOf("预计下月");
                if (nextIdx != -1) {
                    vo.setLastMonth(monthView.substring(0, nextIdx));
                    vo.setNextMonth(monthView.substring(nextIdx));
                } else {
                    vo.setLastMonth(monthView);
                }
            }
            objList.add(vo);
            // 数据入库
            try {
                dataNum = monthForeMapper.save(objList);
            } catch (Exception e) {
                logger.warn("=====Failed to delete tmeplate dir {}", e.getMessage());
                dataNum = -1;
            }
        }
        // 删除下载的文件及创建的临时路径
        try {
            FileUtils.deleteDirectory(FileUtils.getFile(File.separator.concat(uuid)));
        } catch (IOException e) {
            logger.warn("=====Failed to delete tmeplate dir {}", e.getMessage());
        }
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/testrecords.txt"), "更新数据N条：" + dataNum + "\n", Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataNum;
    }
}
