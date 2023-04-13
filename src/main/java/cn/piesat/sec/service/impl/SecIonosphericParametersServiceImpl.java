package cn.piesat.sec.service.impl;

import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.util.ProcessUtil;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class SecIonosphericParametersServiceImpl implements SecIonosphericParametersService {
    private static final Logger logger = LoggerFactory.getLogger(SecIonosphericParametersServiceImpl.class);

    @Autowired
    private SecFileServerProperties secFileServerProperties;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Override
    public SecEnvElementVO getBlinkData(String satcode, String satno, String satfrequency, String startTime, String endTime) {
        SecEnvElementVO eeb = new SecEnvElementVO();
        eeb.setTitleText("电离层闪烁数据");
        String python = secFileServerProperties.getProfile() + secFileServerProperties.getS4FlickerIndexPy();
        String pythonData = secFileServerProperties.getProfile() + secFileServerProperties.getS4DataPath();
        StringBuilder cmd = new StringBuilder("python ");
        satfrequency = StringUtils.isNotEmpty(satfrequency) ? satfrequency.replace(" ", "\" \"") : satfrequency;
        cmd.append(python).append(" \"")
                .append(pythonData).append("\" \"")
                .append(satcode).append("\" \"")
                .append(satno).append("\"  \"")
                .append(satfrequency).append("\"  \"")
                .append(startTime).append("\"  \"")
                .append(endTime).append("\"");

        System.out.println("算法命令=========" + cmd.toString());
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = "";
                while ((line = in.readLine()) != null) {
                    System.out.println("line==========" + line);
                    if (StringUtils.isNotEmpty(line)) {
                        int i1 = line.indexOf("[[");
                        int i2 = line.indexOf("]]") + 2;
                        int i3 = line.lastIndexOf("[[");
                        int i4 = line.lastIndexOf("]]") + 2;
                        String s4a = line.substring(i1, i2);
                        String s4b = line.substring(i3, i4);
                        Object[] dataY = JSON.parseArray(s4a).toArray();
                        Object[] dataY1 = JSON.parseArray(s4b).toArray();
                        eeb.setDataY(Arrays.asList(dataY));
                        eeb.setDataY1(Arrays.asList(dataY1));
                    }
                }
            } catch (Exception e) {
                logger.error(String.format(Locale.ROOT, "====Get flashing data throw exception!!! %s---%s", cmd.toString(), e.getMessage()));
                process.destroy();
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "-------Get ionospheric parameter anomaly %s", e.getMessage()));
        }
        return eeb;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericChineseTECPngs(String altitude, String startTime, String endTime) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecChina());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir, secFileServerProperties.getTecChina());
        if (setPicsPathofMinio(pictures)) return pictures; // 如果已经有图片了就返回图片在oss中的图片数据
        String python = secFileServerProperties.getProfile() + secFileServerProperties.getTecChinaPy();
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" ")
                .append(altitude).append(" \"")
                .append(startTime).append("\" \"")
                .append(endTime).append("\" ")
                .append(targetDir);
        System.out.println("===========" + cmd);
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(100, TimeUnit.SECONDS)) {
                process.destroy();
                logger.error(String.format(Locale.ROOT, "====Execution algorithm timeout!!! %s", cmd.toString()));
            }
            // 设置文件路径
            setPicturesInfo(pictures, targetDir, secFileServerProperties.getTecChina());
            // 上传文件
            updatePicsPathofMinio(pictures);
            // 删除文件
//            FileUtils.deleteQuietly(FileUtils.getFile(targetDir)); // 删除文件每次都会调用python算法重新生成图片
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericGlobalTecPngs(String altitude, String startTime, String endTime) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecGlobal());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir, secFileServerProperties.getTecGlobal());
        if (setPicsPathofMinio(pictures)) return pictures;
        String python = secFileServerProperties.getProfile() + secFileServerProperties.getTecGlobalPy();
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" ")
                .append(altitude).append(" \"")
                .append(startTime).append("\" \"")
                .append(endTime).append("\" ")
                .append(targetDir);
        System.out.println("=========" + cmd.toString());
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(100, TimeUnit.SECONDS)) {
                process.destroy();
                logger.error(String.format(Locale.ROOT, "====Execution algorithm timeout!!! %s", cmd.toString()));
            }
            setPicturesInfo(pictures, targetDir, secFileServerProperties.getTecGlobal());
            updatePicsPathofMinio(pictures);
//            FileUtils.deleteQuietly(FileUtils.getFile(targetDir)); // 删除文件
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    private boolean setPicsPathofMinio(List<SecIonosphericParametersVO> pictures) {
        if (CollectionUtils.isNotEmpty(pictures)) {
            for (SecIonosphericParametersVO pic : pictures) {
                String path = pic.getSrc() != null && pic.getSrc().length() > 0 ? pic.getSrc().substring(1) : pic.getSrc();
                pic.setSrc(OSSInstance.getOSSUtil().preview(bucketName, path));
            }
            return true;
        }
        return false;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericRotiPngs(String startTime, String endTime) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getRotiPics());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir, secFileServerProperties.getRotiPics());
        if (setPicsPathofMinio(pictures)) return pictures;
        String python = secFileServerProperties.getProfile() + secFileServerProperties.getRotiGlobalPy();
        String satelliteData = secFileServerProperties.getProfile() + secFileServerProperties.getRotiSatelliteData();
        String ephemerisData = secFileServerProperties.getProfile() + secFileServerProperties.getRotiEphemerisData();
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" \"")
                .append(satelliteData).append("\" \"")
                .append(ephemerisData).append("\" \"")
                .append(targetDir).append("\" \"")
                .append(startTime).append("\" \"")
                .append(endTime).append("\" ");
        System.out.println("=========" + cmd.toString());
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(100, TimeUnit.SECONDS)) {
                process.destroy();
                logger.error(String.format(Locale.ROOT, "====Execution algorithm timeout!!! %s", cmd.toString()));
            }
            setPicturesInfo(pictures, targetDir, secFileServerProperties.getRotiPics());
            // 算法生成图片上传到文件服务器
            updatePicsPathofMinio(pictures);
//            FileUtils.deleteQuietly(FileUtils.getFile(targetDir)); // 删除文件
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The ROTI is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The ROTI is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    private void updatePicsPathofMinio(List<SecIonosphericParametersVO> pictures) {
        if (CollectionUtils.isNotEmpty(pictures)) {
            for (SecIonosphericParametersVO pic : pictures) {
                OSSInstance.getOSSUtil().upload(bucketName, pic.getSrc(), pic.getSrc());
                String path = pic.getSrc() != null && pic.getSrc().length() > 0 ? FileUtil.rmPathPreSplit(pic.getSrc()) : pic.getSrc();
                pic.setSrc(OSSInstance.getOSSUtil().preview(bucketName, path));
            }
        }
    }

    private void getExistsPicsPathofMinio(List<SecIonosphericParametersVO> pictures) {
        if (CollectionUtils.isNotEmpty(pictures)) {
            for (SecIonosphericParametersVO pic : pictures) {
                String path = pic.getSrc() != null && pic.getSrc().length() > 0 ? FileUtil.rmPathPreSplit(pic.getSrc()) : pic.getSrc();
                pic.setSrc(OSSInstance.getOSSUtil().preview(bucketName, path));
            }
        }
    }

    private void setPicturesInfo(List<SecIonosphericParametersVO> pictures, String targetDir, String pkgs) {
        List<String> pics = FileUtil.getFilePaths(targetDir);
        if (CollectionUtils.isNotEmpty(pics)) {
            pics.forEach(item -> {
                if (item.toLowerCase(Locale.ROOT).endsWith(".png") || item.toLowerCase(Locale.ROOT).endsWith(".jpg") || item.toLowerCase(Locale.ROOT).endsWith(".jpeg")) {
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
