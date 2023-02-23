package cn.piesat.sec.service.impl.dataparse;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.dao.mapper.dataparse.SoF107Mapper;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.model.vo.dataparse.SoF107VO;
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

@Service("soF107Service")
public class SoF107Impl implements SecSpaceEnvData {
    private static final Logger logger = LoggerFactory.getLogger(SoF107Impl.class);
    @Autowired
    private SoF107Mapper soF107Mapper;

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
            List<SoF107VO> objList = new ArrayList<>();
            for (String line : content) {
                String[] lineData = line.split(Constant.DATA_SEPERATOR);
                if (lineData.length >= 2) {
                    SoF107VO vo = new SoF107VO();
                    vo.setTime(DateUtil.parseLocalDateTime(lineData[0], DateConstant.DATE_TIME_PATTERN2));
                    vo.setF107(Double.parseDouble(lineData[1]));
                    objList.add(vo);
                }
            }
            // 数据入库
            try {
                dataNum = soF107Mapper.save(objList);
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
