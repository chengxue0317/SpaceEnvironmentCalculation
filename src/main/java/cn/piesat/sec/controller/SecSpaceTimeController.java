package cn.piesat.sec.controller;

import cn.piesat.sec.comm.constant.KafkaConstant;
import cn.piesat.sec.comm.kafka.KafkaSendServiceImpl;
import cn.piesat.sec.comm.properties.SecMinioProperties;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.FileUtil;
import cn.piesat.sec.comm.util.MinioUtil;
import cn.piesat.sec.model.vo.SecSpaceEnvFileVO;
import cn.piesat.sec.model.vo.SecSpaceFileVO;
import cn.piesat.sec.service.SecSpaceTimeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 空间环境给时空接口
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-11-14 21:11:31
 */
@Api(tags = "空间环境与时空接口")
@RestController
@RequestMapping("/spacetime")
@RequiredArgsConstructor
public class SecSpaceTimeController {
    private final SecSpaceTimeService secSpaceTimeService;
    @Resource
    private MinioUtil minioUtil;

    @Autowired
    private SecMinioProperties secMinioProperties;

    @Autowired
    private KafkaSendServiceImpl kafkaSendService;

    //原是从数据查询时间范围的数据生成文件给时空
//    @GetMapping("fileinfo")
//    public SecSpaceTimeVO getSpaceTimeFileInfo(@RequestParam(value = "fileType", required = true) String fileType,
//                                               @RequestParam(value = "startTime", required = true) String startTime,
//                                               @RequestParam(value = "endTime", required = true) String endTime) {
//        return secSpaceTimeService.getSpaceTimeFileInfo(fileType, startTime, endTime);
//    }

    // 根据条件查找一份文件信息
//    @GetMapping("/fileInformation")
//    public SecSpaceTimeVO getFileInformation(@RequestParam("fileType") String fileType,
//                                             @RequestParam("startTime") String startTime,
//                                             @RequestParam("endTime") String endTime) {
//        return secSpaceTimeService.getFileInformation(fileType, startTime, endTime);
//    }

    // 查数据桶下的所有文件
//    @GetMapping("/list")
////    public List<Object> list() {
////        return secSpaceTimeService.list();
////    }

    // 多文件上传
//    @PostMapping("/uploadFiles")
//    public boolean uploadFiles(@RequestParam("filesPath") List<String> filesPath) {
//        return secSpaceTimeService.uploadFiles(filesPath);
//    }


    // 根据文件全路径下载文件
//    @PostMapping("/downloadFile")
//    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
//        minioUtil.download(secMinioProperties.getBucketName(), fileName, response);
//    }


    /**
     * 上传电离层文件并推送消息
     *
     * @param fileType  文件类型
     * @param filePath  文件路径
     * @param localDate 文件全路径
     * @return STEC、VTEC数据集合
     */
    @ApiOperation("上传电离层文件并推送消息，上传AP/KP/F10.7/电离层数据")
    @PostMapping("sdcsefnotice")
    public String uploadTecData(
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "filePath", required = true) String filePath,
            @RequestParam(value = "localDate", required = true) String localDate) {
        String path = secSpaceTimeService.uploadData(fileType, filePath, localDate);
        if (StringUtils.isEmpty(path)) {
            kafkaSendService.send(KafkaConstant.THEME_TEC, JSON.toJSONString("更新文件失败！！！"));
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("filePath", path);
            map.put("filename", FileUtils.getFile(filePath).getName());
            System.out.println("发送消息=======" + JSON.toJSONString(map));
            kafkaSendService.send(KafkaConstant.THEME_TEC, JSON.toJSONString(map));
        }
        return path;
    }

    /**
     * 上传AP/KP/F10.7数据并推送消息
     *
     * @param localDate 文件全路径
     * @return STEC、VTEC数据集合
     */
    @ApiOperation("上传AP/KP/F10.7数据并推送消息")
    @PostMapping("sdcenvirfile")
    public String uploadSpaceEnvData(@RequestParam(value = "AP", required = true) String rvap,
                                     @RequestParam(value = "KP", required = true) String rvkp,
                                     @RequestParam(value = "F107", required = true) String rvf107,
                                     @RequestParam(value = "localDate", required = true) String localDate) {
        String time = localDate == null || StringUtils.isEmpty(localDate.toString()) ? DateUtil.getToDay() : localDate.toString();
        List<SecSpaceEnvFileVO> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(rvap)) {
            // 上传AP文件
            String pathAp = secSpaceTimeService.uploadData("ap", rvap, time);
            if (StringUtils.isNotEmpty(pathAp)) {
                SecSpaceEnvFileVO apvo = new SecSpaceEnvFileVO();
                apvo.setFileType("AP");
                apvo.setFilePath(pathAp);
                apvo.setFilename(FileUtils.getFile(rvap).getName());
                list.add(apvo);
            }
        }

        if (StringUtils.isNotEmpty(rvkp)) {
            // 上传KP文件
            String pathKp = secSpaceTimeService.uploadData("KP", rvkp, time);
            if (StringUtils.isNotEmpty(pathKp)) {
                SecSpaceEnvFileVO kpvo = new SecSpaceEnvFileVO();
                kpvo.setFileType("KP");
                kpvo.setFilePath(pathKp);
                kpvo.setFilename(FileUtils.getFile(rvkp).getName());
                list.add(kpvo);
            }
        }

        if (StringUtils.isNotEmpty(rvf107)) {
            // 上传F107文件
            String pathF107 = secSpaceTimeService.uploadData("f107", rvf107, time);
            if (StringUtils.isNotEmpty(pathF107)) {
                SecSpaceEnvFileVO f107vo = new SecSpaceEnvFileVO();
                f107vo.setFileType("F107");
                f107vo.setFilePath(pathF107);
                f107vo.setFilename(FileUtils.getFile(rvf107).getName());
                list.add(f107vo);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("fileList", list);
        if (CollectionUtils.isEmpty(list)) {
            kafkaSendService.send(KafkaConstant.THEME_SPACE_ENV, JSON.toJSONString("============更新文件失败！！！" + JSON.toJSONString(list)));
        } else {
            kafkaSendService.send(KafkaConstant.THEME_SPACE_ENV, JSON.toJSONString(map));
        }
        return JSON.toJSONString(map);
    }


//    /**
//     * 模拟时空发送VTEC文件更新信息
//     *
//     * @param fileType  文件类型
//     * @param filePath  文件路径
//     * @param localDate 文件全路径
//     * @return STEC、VTEC数据集合
//     */
//    @ApiOperation("模拟时空发送VTEC文件更新信息")
//    @PostMapping("vtecData")
//    public String uploadVTecData(
//            @RequestParam(value = "fileType", required = true) String fileType,
//            @RequestParam(value = "filePath", required = true) String filePath,
//            @RequestParam(value = "localDate", required = true) String localDate) {
//        // 上传AP文件
//        String pathAp = secSpaceTimeService.uploadData("ap", "/dataTest/GmAp20221219000000.txt", localDate);
//        // 上传KP文件
//        String pathKp = secSpaceTimeService.uploadData("kp", "/dataTest/GmKp20221219000000.txt", localDate);
//        // 上传F107文件
//        String pathF107 = secSpaceTimeService.uploadData("f107", "/dataTest/SoF10720221219000000.txt", localDate);
//
//        List<SecSpaceEnvFileVO> list = new ArrayList<>();
//        if (StringUtils.isNotEmpty(pathAp)) {
//            SecSpaceEnvFileVO apvo = new SecSpaceEnvFileVO();
//            apvo.setFileType("AP");
//            apvo.setFilePath(pathAp);
//            apvo.setFilename("GmAp20221219000000.txt");
//            list.add(apvo);
//        }
//        if (StringUtils.isNotEmpty(pathKp)) {
//            SecSpaceEnvFileVO kpvo = new SecSpaceEnvFileVO();
//            kpvo.setFileType("KP");
//            kpvo.setFilePath(pathKp);
//            kpvo.setFilename("GmKp20221219000000.txt");
//            list.add(kpvo);
//        }
//        if (StringUtils.isNotEmpty(pathF107)) {
//            SecSpaceEnvFileVO f107vo = new SecSpaceEnvFileVO();
//            f107vo.setFileType("F107");
//            f107vo.setFilePath(pathF107);
//            f107vo.setFilename("SoF10720221219000000");
//            list.add(f107vo);
//        }
//
//        if (CollectionUtils.isEmpty(list)) {
//            kafkaSendService.send(KafkaConstant.THEME_CMS_STIP_SEG, JSON.toJSONString("更新文件失败！！！"));
//        } else {
//            kafkaSendService.send(KafkaConstant.THEME_CMS_STIP_SEG, JSON.toJSONString(list));
//        }
//        return "ap:" + pathAp + ";kp:" + pathKp + ";f107:" + pathF107;
//    }

}
