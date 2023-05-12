package cn.piesat.sec.service.impl;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.kafka.KafkaSendServiceImpl;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.service.SecSpaceTimeService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SecSpaceTimeServiceImpl implements SecSpaceTimeService {
    private static Logger logger = LoggerFactory.getLogger(SecSpaceTimeServiceImpl.class);
    @Autowired
    private KafkaSendServiceImpl kafkaSendService;

    @Value("${s3.bucketName}")
    private String buketName;

    @Value("${piesat.profile}")
    private String profile;

    @Override
    public String uploadData(String fileType, String filePath, String localDate) {
        File file = FileUtils.getFile(filePath);
        String uploadPath = profile.concat(fileType).concat(Constant.FILE_SEPARATOR).concat(localDate.replaceAll("-", "/")).concat(Constant.FILE_SEPARATOR).concat(file.getName());
        OSSInstance.getOSSUtil().upload(buketName, uploadPath, filePath);
        if (OSSInstance.getOSSUtil().doesObjectExist(buketName, uploadPath)) {
            return uploadPath;
        } else {
            return null;
        }
    }

    @Override
    public Boolean giveNotice(String topic, String message) {
        return kafkaSendService.send(topic, message);
    }

}
