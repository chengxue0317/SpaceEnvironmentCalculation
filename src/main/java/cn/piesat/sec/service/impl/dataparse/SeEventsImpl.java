package cn.piesat.sec.service.impl.dataparse;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.dao.mapper.dataparse.SeEventlMapper;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.model.vo.dataparse.SecEventsVO;
import cn.piesat.sec.service.SecSpaceEnvData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("seEventsService")
public class SeEventsImpl implements SecSpaceEnvData {
    private static final Logger logger = LoggerFactory.getLogger(SeEventsImpl.class);
    @Autowired
    private SeEventlMapper seEventlMapper;

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
        if (CollectionUtils.isNotEmpty(content)) {
            for (String line : content) {
                String[] lineData = line.split(Constant.DATA_SEPERATOR);
                if (lineData.length >= 3) {
                    SecEventsVO vo = new SecEventsVO();
                    vo.setTime(DateUtil.parseLocalDateTime(lineData[0], DateConstant.DATE_TIME_PATTERN2));
                    vo.setContent(lineData[2]);
                    int level = 0;
                    String ct = lineData[2];
                    if (ct.indexOf("[黄]") != -1) {
                        level = 1;
                    }
                    if (ct.indexOf("[橙]") != -1) {
                        level = 2;
                    }
                    if (ct.indexOf("[红]") != -1) {
                        level = 3;
                    }
                    vo.setLevel(level);
                    // 数据入库
                    try {
                        if (ct.indexOf("耀斑") != -1) {
                            dataNum = seEventlMapper.save(vo, "SEC_XRAY_ALARM");
                        } else if (ct.indexOf("质子") != -1) {
                            dataNum = seEventlMapper.save(vo, "SEC_PROTON_ALARM");
                        } else if (ct.indexOf("地磁") != -1) {
                            dataNum = seEventlMapper.save(vo, "SEC_DST_ALARM");
                        } else {
                            dataNum = seEventlMapper.save(vo, "SEC_ELE_ALARM");
                        }
                    } catch (Exception e) {
                        logger.warn("=====Failed to delete tmeplate dir {}", e.getMessage());
                        dataNum = -1;
                    }
                }
            }

        }
        // 删除下载的文件及创建的临时路径
        try {
            FileUtils.deleteDirectory(FileUtils.getFile(File.separator.concat(uuid)));
        } catch (IOException e) {
            logger.warn("=====Failed to delete tmeplate dir {}", e.getMessage());
        }
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/Eventrecords.txt"), "更新数据N条：" + dataNum + "\n", Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataNum;
    }
}
