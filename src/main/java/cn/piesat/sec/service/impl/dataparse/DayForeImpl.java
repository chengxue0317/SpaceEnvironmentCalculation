package cn.piesat.sec.service.impl.dataparse;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.dao.mapper.dataparse.DayForeMapper;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.model.vo.dataparse.DayForeVo;
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

@Service("DayForeService")
public class DayForeImpl implements SecSpaceEnvData {
    private static final Logger logger = LoggerFactory.getLogger(SoF107Impl.class);
    @Autowired
    private DayForeMapper dayForeMapper;

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
            List<DayForeVo> objList = new ArrayList<>();
            for (String line : content) {
                String[] lineData = line.split(Constant.DATA_SEPERATOR);
                if (lineData.length >= 25) {
                    DayForeVo vo = new DayForeVo();
                    vo.setTime(DateUtil.parseLocalDateTime(lineData[0], DateConstant.DATE_TIME_PATTERN2));
                    vo.setAp1(lineData[1] != null ? Double.parseDouble(lineData[1]) : 0.0);
                    vo.setAp2(lineData[2] != null ? Double.parseDouble(lineData[2]) : 0.0);
                    vo.setAp3(lineData[3] != null ? Double.parseDouble(lineData[3]) : 0.0);
                    vo.setF1071(lineData[4] != null ? Double.parseDouble(lineData[4]) : 0.0);
                    vo.setF1072(lineData[5] != null ? Double.parseDouble(lineData[5]) : 0.0);
                    vo.setF1073(lineData[6] != null ? Double.parseDouble(lineData[6]) : 0.0);
                    vo.setSpe1(lineData[7]);
                    vo.setSpe2(lineData[8]);
                    vo.setSpe3(lineData[9]);
                    vo.setRee1(lineData[10]);
                    vo.setRee2(lineData[11]);
                    vo.setRee3(lineData[12]);
                    vo.setGsm1(lineData[13]);
                    vo.setGsm2(lineData[14]);
                    vo.setGsm3(lineData[15]);
                    vo.setGsma1(lineData[16]);
                    vo.setGsma1(lineData[17]);
                    vo.setGsma1(lineData[18]);
                    vo.setSxrm1(lineData[19]);
                    vo.setSxrm2(lineData[20]);
                    vo.setSxrm3(lineData[21]);
                    vo.setSxrm1(lineData[22]);
                    vo.setSxrm2(lineData[23]);
                    vo.setSxrm3(lineData[24]);
                    String overview = lineData[lineData.length - 1];
                    if (StringUtils.isNotEmpty(overview)) {
                        int aft3d = overview.indexOf("预计未来三天");
                        if (aft3d != -1) {
                            vo.setBef24h(overview.substring(0, aft3d));
                            vo.setAft3d(overview.substring(aft3d));
                        } else {
                            vo.setBef24h(overview);
                        }
                    }

                    objList.add(vo);
                }
            }
            // 数据入库
            try {
                dataNum = dayForeMapper.save(objList);
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
