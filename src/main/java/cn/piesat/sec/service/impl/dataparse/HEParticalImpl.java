package cn.piesat.sec.service.impl.dataparse;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.dao.mapper.dataparse.HEParticalMapper;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.model.vo.dataparse.SecHEParticalVO;
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

@Service("heParticalService")
public class HEParticalImpl implements SecSpaceEnvData {
    private static final Logger logger = LoggerFactory.getLogger(HEParticalImpl.class);
    @Autowired
    private HEParticalMapper heParticalMapper;

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
            List<SecHEParticalVO> objList = new ArrayList<>();
            for (String line : content) {
                String[] lineData = line.split(Constant.DATA_SEPERATOR);
                if (lineData.length >= 18) { // 接口文件和数据库表不一致，这里的判断条件临时设置到E2数据
                    SecHEParticalVO vo = new SecHEParticalVO();
                    vo.setTime(DateUtil.parseLocalDateTime(lineData[0], DateConstant.DATE_TIME_PATTERN2));
                    vo.setP10(Double.parseDouble(lineData[1]));
                    vo.setP50(Double.parseDouble(lineData[2]));
                    vo.setP100(Double.parseDouble(lineData[3]));
                    vo.setE2(Double.parseDouble(lineData[4]));
                    vo.setPdiff1(Double.parseDouble(lineData[5]));
                    vo.setPdiff2a(Double.parseDouble(lineData[6]));
                    vo.setPdiff2b(Double.parseDouble(lineData[7]));
                    vo.setPdiff3(Double.parseDouble(lineData[8]));
                    vo.setPdiff4(Double.parseDouble(lineData[9]));
                    vo.setPdiff5(Double.parseDouble(lineData[10]));
                    vo.setPdiff6(Double.parseDouble(lineData[11]));
                    vo.setPdiff7(Double.parseDouble(lineData[12]));
                    vo.setPdiff8a(Double.parseDouble(lineData[13]));
                    vo.setPdiff8b(Double.parseDouble(lineData[14]));
                    vo.setPdiff8c(Double.parseDouble(lineData[15]));
                    vo.setPdiff9(Double.parseDouble(lineData[16]));
                    vo.setPdiff10(Double.parseDouble(lineData[17]));
                    objList.add(vo);
                }
            }
            // 数据入库
            try {
                dataNum = heParticalMapper.save(objList);
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
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/HEParticalrecords.txt"), "更新数据N条：" + dataNum + "\n", Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataNum;
    }
}
