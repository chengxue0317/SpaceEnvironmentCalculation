package cn.piesat.sec.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时删除文件
 */
@Configuration
@EnableScheduling
public class DeleteFile {

    private static final Logger log = LoggerFactory.getLogger(DeleteFile.class);

    @Value("${picture.path.magnetic_global_v2}")
    private String pictureMagneticGlobalV2;
    @Value("${picture.path.magnetic_global}")
    private String picturePathMagneticGlobal;
    @Value("${picture.path.satellite_radiation_env}")
    private String picturePathSatelliteRadiationEnv;
    @Value("${picture.path.global_radiation_env}")
    private String picturePathGlobalRadiationEnv;
    @Value("${picture.path.atmosphere_density_global}")
    private String picturePathAtmosphereDensityGlobal;

//    每天凌晨1点执行一次：0 0 1 * * ?
    @Scheduled(cron = "0 0 1 * * ?")
    private void configureTasks() {
        log.info("定时任务-删除过期图片开始执行！");
        DeleteExpiredFile deleteExpiredFile = new DeleteExpiredFile();
        deleteExpiredFile.deleteExpiredFileTask(pictureMagneticGlobalV2);
        deleteExpiredFile.deleteExpiredFileTask(picturePathMagneticGlobal);
        deleteExpiredFile.deleteExpiredFileTask(picturePathSatelliteRadiationEnv);
        deleteExpiredFile.deleteExpiredFileTask(picturePathGlobalRadiationEnv);
        deleteExpiredFile.deleteExpiredFileTask(picturePathAtmosphereDensityGlobal);
        log.info("定时任务-删除过期图片执行完毕！");
    }

}


