package cn.piesat.sec.service.impl;

import cn.piesat.sec.comm.conf.SecFileServerConfig;
import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.DateConstant;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.util.ProcessUtil;
import cn.piesat.sec.dao.mapper.SecIonosphericParametersMapper;
import cn.piesat.sec.model.entity.SecBxyzDO;
import cn.piesat.sec.model.entity.SecIonosphericParamtersDO;
import cn.piesat.sec.model.vo.SecEnvElementVO;
import cn.piesat.sec.model.vo.SecIonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class SecIonosphericParametersServiceImpl implements SecIonosphericParametersService {
    private static final Logger logger = LoggerFactory.getLogger(SecIonosphericParametersServiceImpl.class);

    @Autowired
    private SecIonosphericParametersMapper secIonoMapper;

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
        String targetDir = SecFileServerConfig.getProfile().concat(SecFileServerConfig.getTecStations());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹

        setPicturesInfo(pictures, targetDir, SecFileServerConfig.getTecStations());
        if (CollectionUtils.isNotEmpty(pictures)) {
            return pictures;
        }
        String python = SecFileServerConfig.getProfile() + "algorithm/stationtecpng/TEC_keshihua_03_png_creation.py";
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" ").append(targetDir);
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(100, TimeUnit.SECONDS)) {
                process.destroy();
            }
            setPicturesInfo(pictures, targetDir, SecFileServerConfig.getTecStations());
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
        String targetDir = SecFileServerConfig.getProfile().concat(SecFileServerConfig.getTecTimes());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir.concat(SecFileServerConfig.getSecondDir()), SecFileServerConfig.getTecTimes().concat(SecFileServerConfig.getSecondDir()));
        if (CollectionUtils.isNotEmpty(pictures)) {
            return pictures;
        }
        String python = SecFileServerConfig.getProfile() + "algorithm/tecpng/TEC/parameter_maps.py";
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" \"")
                .append(startTime).append("\" \"")
                .append(endTime).append("\" ")
                .append(targetDir);
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(600, TimeUnit.SECONDS)) {
                process.destroy();
            }
            setPicturesInfo(pictures, targetDir.concat(SecFileServerConfig.getSecondDir()), SecFileServerConfig.getTecTimes().concat(SecFileServerConfig.getSecondDir()));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    @Override
    public List<SecIonosphericParametersVO> getIonosphericRotiPngs(String startTime, String endTime, String staId) {
        List<SecIonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = SecFileServerConfig.getProfile().concat(SecFileServerConfig.getRoti());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir.concat(SecFileServerConfig.getSecondDir()), SecFileServerConfig.getRoti().concat(SecFileServerConfig.getSecondDir()));
        if (CollectionUtils.isNotEmpty(pictures)) {
            return pictures;
        }
        String python = SecFileServerConfig.getProfile() + "algorithm/roti/ROTI/parameter_maps.py";
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" \"")
                .append(startTime).append("\" \"")
                .append(endTime).append("\" ")
                .append(targetDir);
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(600, TimeUnit.SECONDS)) {
                process.destroy();
            }
            setPicturesInfo(pictures, targetDir.concat(SecFileServerConfig.getSecondDir()), SecFileServerConfig.getRoti().concat(SecFileServerConfig.getSecondDir()));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    private void setPicturesInfo(List<SecIonosphericParametersVO> pictures, String targetDir, String pkgs) {
        File[] pics = FileUtils.getFile(targetDir).listFiles();
        if (ArrayUtils.isNotEmpty(pics)) {
            String urlHead = "http://".concat(SecFileServerConfig.getIp()).concat(":").concat(SecFileServerConfig.getPort()).concat(Constant.FILE_SEPARATOR);
            Arrays.stream(pics).forEach(item -> {
                if (item.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
                    SecIonosphericParametersVO ispvo = new SecIonosphericParametersVO();
                    ispvo.setName(item.getName().substring(0, item.getName().indexOf(".")));
                    ispvo.setSrc(urlHead.concat(pkgs).concat(item.getName()));
                    pictures.add(ispvo);
                }
            });
        }
    }
}
