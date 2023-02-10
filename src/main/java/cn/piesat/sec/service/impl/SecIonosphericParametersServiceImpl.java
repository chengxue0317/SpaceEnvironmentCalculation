package cn.piesat.sec.service.impl;

import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.comm.properties.SecMinioProperties;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.util.MinioUtil;
import cn.piesat.sec.comm.util.ProcessUtil;
import cn.piesat.sec.dao.mapper.SecIonosphericParametersMapper;
import cn.piesat.sec.model.entity.SecIonosphericParamtersDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class SecIonosphericParametersServiceImpl implements SecIonosphericParametersService {
    private static final Logger logger = LoggerFactory.getLogger(SecIonosphericParametersServiceImpl.class);

    @Autowired
    private SecFileServerProperties secFileServerProperties;

    @Autowired
    private SecIonosphericParametersMapper secIonoMapper;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private SecMinioProperties secMinioProperties;

    @Override
    public SecEnvElementVO getBlinkData(String staId, String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("电离层闪烁数据");
        try {
            List<Object> dataX = new ArrayList<>();
            List<Object> dataY = new ArrayList<>();
            List<Object> dataY1 = new ArrayList<>();
            List<Object> dataY2 = new ArrayList<>();
            List<SecIonosphericParamtersDO> list = secIonoMapper.getBlinkData(staId, startTime, endTime);
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(item -> {
                    String time = item.getTime().format(DateTimeFormatter.ofPattern(DateConstant.DATE_TIME_PATTERN));
                    dataX.add(item.getTime());
                    dataY.add(new String[]{time, item.getLs4()});
                    dataY1.add(new String[]{time, item.getSs4()});
                    dataY2.add(new String[]{time, item.getUhfs4()});
                });
                eeb.setDataX(dataX);
                eeb.setDataY(dataY);
                eeb.setDataY1(dataY1);
                eeb.setDataY2(dataY2);
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get ionospheric parameter anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericStationsTECPngs() {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecStations());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir, secFileServerProperties.getTecStations());
        if (setPicsPathofMinio(pictures)) return pictures;
        String python = secFileServerProperties.getProfile() + secFileServerProperties.getTecStationPy();
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" ").append(targetDir);
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(100, TimeUnit.SECONDS)) {
                process.destroy();
                logger.error(String.format(Locale.ROOT, "====Execution algorithm timeout!!! %s", cmd.toString()));
            }
            setPicturesInfo(pictures, targetDir, secFileServerProperties.getTecStations());
            updatePicsPathofMinio(pictures);
            FileUtils.deleteQuietly(FileUtils.getFile(targetDir)); // 删除文件
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericTecPngs(String startTime, String endTime) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecTimes());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir, secFileServerProperties.getTecTimes().concat(secFileServerProperties.getSecondDir()));
//        if (setPicsPathofMinio(pictures)) return pictures;
//        String python = secFileServerProperties.getProfile() + secFileServerProperties.getTecGLStationPy();
//        StringBuilder cmd = new StringBuilder("python ");
//        cmd.append(python).append(" \"")
//                .append(startTime).append("\" \"")
//                .append(endTime).append("\" ")
//                .append(targetDir);
//        try {
//            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
//            if (!process.waitFor(600, TimeUnit.SECONDS)) {
//                process.destroy();
//                logger.error(String.format(Locale.ROOT, "====Execution algorithm timeout!!! %s", cmd.toString()));
//            }
//            setPicturesInfo(pictures, targetDir.concat(secFileServerProperties.getSecondDir()), secFileServerProperties.getTecTimes().concat(secFileServerProperties.getSecondDir()));
            updatePicsPathofMinio(pictures);
//            FileUtils.deleteQuietly(FileUtils.getFile(targetDir)); // 删除文件
//        } catch (IOException e) {
//            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
//        } catch (InterruptedException e) {
//            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
//        }
        return pictures;
    }

    private boolean setPicsPathofMinio(List<SecIonosphericParametersVO> pictures) {
        if (CollectionUtils.isNotEmpty(pictures)) {
            for (SecIonosphericParametersVO pic : pictures) {
                String path = pic.getSrc() != null && pic.getSrc().length() > 0 ? pic.getSrc().substring(1) : pic.getSrc();
                pic.setSrc(minioUtil.preview(secMinioProperties.getBucketName(), path));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericRotiPngs(String startTime, String endTime, String staId) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getRoti());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir, secFileServerProperties.getRoti().concat(secFileServerProperties.getSecondDir()));
//        if (setPicsPathofMinio(pictures)) return pictures;
//        String python = secFileServerProperties.getProfile() + secFileServerProperties.getRotiGLStationPy();
//        StringBuilder cmd = new StringBuilder("python ");
//        cmd.append(python).append(" \"")
//                .append(startTime).append("\" \"")
//                .append(endTime).append("\" ")
//                .append(targetDir);
//        try {
//            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
//            if (!process.waitFor(600, TimeUnit.SECONDS)) {
//                process.destroy();
//                logger.error(String.format(Locale.ROOT, "====Execution algorithm timeout!!! %s", cmd.toString()));
//            }
//            setPicturesInfo(pictures, targetDir.concat(secFileServerProperties.getSecondDir()), secFileServerProperties.getRoti().concat(secFileServerProperties.getSecondDir()));
            // 算法生成图片上传到文件服务器
            updatePicsPathofMinio(pictures);
//            FileUtils.deleteQuietly(FileUtils.getFile(targetDir)); // 删除文件
//        } catch (IOException e) {
//            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
//        } catch (InterruptedException e) {
//            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
//        }
        return pictures;
    }

    private void updatePicsPathofMinio(List<SecIonosphericParametersVO> pictures) {
        if (CollectionUtils.isNotEmpty(pictures)) {
            for (SecIonosphericParametersVO pic : pictures) {
                minioUtil.upload(secMinioProperties.getBucketName(), pic.getSrc(), pic.getSrc());
                String path = pic.getSrc() != null && pic.getSrc().length() > 0 ? pic.getSrc().substring(1) : pic.getSrc();
                pic.setSrc(minioUtil.preview(secMinioProperties.getBucketName(), path));
            }
        }
    }

    private void setPicturesInfo(List<SecIonosphericParametersVO> pictures, String targetDir, String pkgs) {
        List<String> pics = FileUtil.getFilePaths(targetDir);
        if (CollectionUtils.isNotEmpty(pics)) {
            pics.forEach(item -> {
                if (item.toLowerCase(Locale.ROOT).endsWith(".png")) {
                    SecIonosphericParametersVO ispvo = new SecIonosphericParametersVO();
                    File file = FileUtils.getFile(item);
                    ispvo.setName(file.getName().substring(0, file.getName().indexOf(".")));
                    ispvo.setSrc(secFileServerProperties.getProfile().concat(pkgs).concat(file.getName()));
                    pictures.add(ispvo);
                }
            });
        } else {
            logger.info(String.format(Locale.ROOT, "=====路径%s下没有找到文件=====", targetDir));
        }
    }
}
