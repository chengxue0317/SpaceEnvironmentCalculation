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
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecChina());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        List<String> fileNames = FileUtil.picsNames(altitude, startTime, endTime);
        boolean allPicturesExists = isAllPicturesExists(fileNames, targetDir);
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        if (allPicturesExists) {
            System.out.println("-----------真是太幸运了，所有的文件都存在");
            setOSSPicturesInfo(pictures, fileNames, targetDir);
            return pictures;
        }

        List<SecIonosphericParametersVO> secVoslist = drawTecChinaPictures(startTime, endTime, altitude, targetDir);
        pictures.addAll(secVoslist);
        return dataClean(pictures);
    }

    private List<SecIonosphericParametersVO> drawTecChinaPictures(String startTime, String endTime, String altitude, String targetDir) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
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
            if (!process.waitFor(300, TimeUnit.SECONDS)) {
                process.destroy();
                logger.error(String.format(Locale.ROOT, "====Execution algorithm drawTecChinaPictures timeout!!! %s", cmd.toString()));
            }
            List<String> names = FileUtil.picsNames(altitude, startTime, endTime);
            // 设置文件路径
            setPicturesInfo(pictures, names, targetDir);
            updatePicsPathofMinio(pictures);
            names.forEach(name -> {
                FileUtils.deleteQuietly(FileUtils.getFile(targetDir.concat(name)));
            });

        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The chinese tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The chinese tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericGlobalTecPngs(String altitude, String startTime, String endTime) {
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getTecGlobal());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        List<String> fileNames = FileUtil.picsNames(altitude, startTime, endTime);
        boolean allPicturesExists = isAllPicturesExists(fileNames, targetDir);
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        if (allPicturesExists) {
            System.out.println("-----------真是太幸运了，所有的文件都存在");
            setOSSPicturesInfo(pictures, fileNames, targetDir);
            return pictures;
        }

        List<SecIonosphericParametersVO> secVoslist = drawTecglobalPictures(startTime, endTime, altitude, targetDir);
        pictures.addAll(secVoslist);
        return dataClean(pictures);
    }

    private List<SecIonosphericParametersVO> drawTecglobalPictures(String startTime, String endTime, String altitude, String targetDir) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String python = secFileServerProperties.getProfile() + secFileServerProperties.getTecGlobalPy();
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" ")
                .append(altitude).append(" \"")
                .append(startTime).append("\" \"")
                .append(endTime).append("\" ")
                .append(targetDir);
        System.out.println("===========" + cmd);
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(500, TimeUnit.SECONDS)) {
                process.destroy();
                logger.error(String.format(Locale.ROOT, "====Execution algorithm drawTecglobalPictures timeout!!! %s", cmd.toString()));
            }
            List<String> names = FileUtil.picsNames(altitude, startTime, endTime);
            // 设置文件路径
            setPicturesInfo(pictures, names, targetDir);
            updatePicsPathofMinio(pictures);
            names.forEach(name -> {
                FileUtils.deleteQuietly(FileUtils.getFile(targetDir.concat(name)));
            });
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericRotiPngs(String startTime, String endTime) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = secFileServerProperties.getProfile().concat(secFileServerProperties.getRotiPics());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        List<String> fileNames = FileUtil.picturesNamesMinutes(startTime, endTime);
        boolean allPicturesExists = isAllPicturesExists(fileNames, targetDir);
        if (allPicturesExists) {
            System.out.println("-----------真是太幸运了，所有的文件都存在");
            setOSSPicturesInfo(pictures, fileNames, targetDir);
            return pictures;
        }

        List<SecIonosphericParametersVO> secVoslist = drawROTIPictures(startTime, endTime, targetDir);
        pictures.addAll(secVoslist);
        return dataClean(pictures);
    }

    private List<SecIonosphericParametersVO> drawROTIPictures(String startTime, String endTime, String targetDir) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
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
            List<String> fileNames = FileUtil.picturesNamesMinutes(startTime, endTime);
            setPicturesInfo(pictures, fileNames, targetDir);
            updatePicsPathofMinio(pictures);
            fileNames.forEach(name -> {
                FileUtils.deleteQuietly(FileUtils.getFile(targetDir.concat(name)));
            });
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
                System.out.println("---上传文件路径---" + pic.getSrc());
                if (FileUtils.getFile(pic.getSrc()).exists()) {
                    OSSInstance.getOSSUtil().upload(bucketName, pic.getSrc(), pic.getSrc());
                    String path = pic.getSrc() != null && pic.getSrc().length() > 0 ? FileUtil.rmPathPreSplit(pic.getSrc()) : pic.getSrc();
                    pic.setSrc(OSSInstance.getOSSUtil().preview(bucketName, path));
                }
            }
        }
    }

    private void setOSSPicturesInfo(List<SecIonosphericParametersVO> pictures, List<String> fileNames, String targetDir) {
        fileNames.forEach(name -> {
            SecIonosphericParametersVO pic = new SecIonosphericParametersVO();
            pic.setName(name.substring(0, name.indexOf(".")));
            pic.setSrc(OSSInstance.getOSSUtil().preview(bucketName, targetDir.concat(name)));
            pictures.add(pic);
        });
    }

    private void setPicturesInfo(List<SecIonosphericParametersVO> pictures, List<String> fileNames, String targetDir) {
        fileNames.forEach(name -> {
            SecIonosphericParametersVO pic = new SecIonosphericParametersVO();
            pic.setName(name.substring(0, name.indexOf(".")));
            pic.setSrc(targetDir.concat(name));
            pictures.add(pic);
        });
    }

    private static List<String> splitLocalDateTimeByHour(LocalDateTime start, LocalDateTime end, int hour) {
        List<String> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int between = (int) ChronoUnit.HOURS.between(start, end);
        if (hour > 0 && between > hour) {
            int num = (int) Math.ceil(between / (hour * 1.0));
            if (num > 1) {
                LocalDateTime temp = LocalDateTime.of(start.getYear(), start.getMonth(), start.getDayOfMonth(), start.getHour(), 0, 0);
                list.add(start.format(formatter));
                for (int i = 0; i < num - 1; i++) {
                    temp = temp.plusHours(hour);
                    list.add(temp.format(formatter));
                }
                list.add(end.format(formatter));
            }
        } else {
            list.add(start.format(formatter));
            list.add(end.format(formatter));
        }
        return list;
    }

    /**
     * 判断本次要生成的图片是否全都存在
     *
     * @param nameList 文件命令
     * @param tardir   目标文件所在位置
     * @return 文件是否都存在
     */
    private boolean isAllPicturesExists(List<String> nameList, String tardir) {
        for (String name : nameList) {
            boolean b = OSSInstance.getOSSUtil().doesObjectExist(bucketName, tardir.concat(name));
            if (!b) {
                return false;
            }
        }
        return true;
    }


    private List<SecIonosphericParametersVO> dataClean(List<SecIonosphericParametersVO> list) {
        return list.stream().filter(item -> item.getSrc().startsWith("data:")).collect(Collectors.toList());
    }

}
