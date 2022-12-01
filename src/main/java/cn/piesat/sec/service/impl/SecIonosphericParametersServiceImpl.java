package cn.piesat.sec.service.impl;

import cn.piesat.sec.comm.conf.SecFileServerConfig;
import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.util.ProcessUtil;
import cn.piesat.sec.model.vo.IonosphericParametersVO;
import cn.piesat.sec.service.SecIonosphericParametersService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class SecIonosphericParametersServiceImpl implements SecIonosphericParametersService {
    private static final Logger logger = LoggerFactory.getLogger(SecIonosphericParametersServiceImpl.class);

    @Override
    public List<IonosphericParametersVO> getIonosphericStationsTECPngs() {
        List<IonosphericParametersVO> pictures = new ArrayList<>();
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
    public List<IonosphericParametersVO> getIonosphericTecPngs(String startTime, String endTime) {
        List<IonosphericParametersVO> pictures = new ArrayList<>();
        String targetDir = SecFileServerConfig.getProfile().concat(SecFileServerConfig.getTecTimes());
        FileUtil.mkdirs(targetDir); // 如果文件夹不存在则创建文件夹
        setPicturesInfo(pictures, targetDir, SecFileServerConfig.getTecTimes());
        if (CollectionUtils.isNotEmpty(pictures)) {
            return pictures;
        }
        String python = SecFileServerConfig.getProfile() + "algorithm/tecpng/TEC.py";
        StringBuilder cmd = new StringBuilder("python ");
        cmd.append(python).append(" \"")
                .append(startTime).append("\" \"")
                .append(endTime).append("\" ")
                .append(targetDir);
        try {
            Process process = Runtime.getRuntime().exec(ProcessUtil.getCommand(cmd.toString()));
            if (!process.waitFor(100, TimeUnit.SECONDS)) {
                process.destroy();
            }
            setPicturesInfo(pictures, targetDir, SecFileServerConfig.getTecTimes());
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------The global tec site image is abnormal. %s", e.getMessage()));
        } catch (InterruptedException e) {
            logger.info(String.format(Locale.ROOT, "-----The global tec site image is abnormal. %s", e.getMessage()));
        }
        return pictures;
    }

    private void setPicturesInfo(List<IonosphericParametersVO> pictures, String targetDir, String pkgs) {
        File[] pics = FileUtils.getFile(targetDir).listFiles();
        if (ArrayUtils.isNotEmpty(pics)) {
            String urlHead = "http://".concat(SecFileServerConfig.getIp()).concat(":").concat(SecFileServerConfig.getPort()).concat(Constant.FILE_SEPARATOR);
            Arrays.stream(pics).forEach(item -> {
                if (item.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
                    IonosphericParametersVO ispvo = new IonosphericParametersVO();
                    ispvo.setName(item.getName().substring(0, item.getName().indexOf(".")));
                    ispvo.setSrc(urlHead.concat(pkgs).concat(item.getName()));
                    pictures.add(ispvo);
                }
            });
        }
    }
}
