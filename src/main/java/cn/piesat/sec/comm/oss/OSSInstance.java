package cn.piesat.sec.comm.oss;

import cn.piesat.sec.comm.config.SecOSSConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class OSSInstance implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private static IFileSystem iFileSystem;

    @Autowired
    private SecOSSConfig secOSSConfig;

    @PostConstruct
    public void OSSInstance(){
        if("minio".equals(secOSSConfig.getOssType())){
            iFileSystem = applicationContext.getBean("minioFileSystem", IFileSystem.class);
        } else {
            iFileSystem = applicationContext.getBean("s3FileSystem", IFileSystem.class);
        }
        log.info("iFileSystem is {}",iFileSystem);
    }

    public static IFileSystem getOSSUtil(){
        return iFileSystem;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
