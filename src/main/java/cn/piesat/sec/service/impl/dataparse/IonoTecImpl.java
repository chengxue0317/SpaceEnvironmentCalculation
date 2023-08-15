package cn.piesat.sec.service.impl.dataparse;

import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.util.ProcessUtil;
import cn.piesat.sec.dao.mapper.dataparse.IonoTecMapper;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.service.SecSpaceEnvData;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service("IonoTecService")
public class IonoTecImpl implements SecSpaceEnvData {
    private static final Logger logger = LoggerFactory.getLogger(SoF107Impl.class);

    @Autowired
    private SecFileServerProperties secFileServerProperties;

    @Override
    public int parseData(SecIISVO secIISVO) {
        // 下载文件
        String uuid = UUID.randomUUID().toString();
        OSSInstance.getOSSUtil().download(secIISVO.getBucketName(), secIISVO.getKey(), uuid);
        // 解析文件数据
        String filePath = File.separator.concat(uuid).concat(File.separator).concat(secIISVO.getKey());
        filePath = FileUtil.checkPath(filePath);
        filePath = FileUtils.getFile(filePath).getParent();
        String python = secFileServerProperties.getProfile() + secFileServerProperties.getParseTectoDBpy();
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" \"")
                .append(filePath).append("\" \"")
                .append(secFileServerProperties.getWeekPngIni()).append("\"");
        int dataNum = 0;
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(300, TimeUnit.SECONDS)) {
                process.destroy();
                dataNum = -1;
                logger.error(String.format(Locale.ROOT, "====Execution algorithm parse tec data timeout!!! %s", cmd.toString()));
            }
        } catch (IOException e) {
            dataNum = -1;
            logger.error(String.format(Locale.ROOT, "-------The parse tec data is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            dataNum = -1;
            logger.info(String.format(Locale.ROOT, "-----The parse tec data is abnormal. %s", e.getMessage()));
        }
        // 删除下载的文件及创建的临时路径
        try {
            FileUtils.deleteDirectory(FileUtils.getFile(File.separator.concat(uuid)));
        } catch (IOException e) {
            dataNum = -1;
            logger.warn("=====Failed to delete tmeplate dir {}", e.getMessage());
        }
        return dataNum;
    }
}
