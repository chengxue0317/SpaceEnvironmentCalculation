package cn.piesat.sec.controller;

import cn.piesat.sec.comm.config.SecMinioConfig;
import cn.piesat.sec.comm.properties.SecMinioProperties;
import cn.piesat.sec.comm.util.MinioUtil;
import cn.piesat.sec.model.vo.SecSpaceTimeVO;
import cn.piesat.sec.service.SecSpaceTimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
     * 获取时空上传的GNSS电离层STEC、VTEC的数据
     *
     * @param fileType  文件类型
     * @param localDate 数据日期
     * @return STEC、VTEC数据集合
     */
    @ApiOperation("GNSS电离层STEC、VTEC的数据")
    @GetMapping("svtecData")
    public List<String> getSvtecData(@RequestParam(value = "fileType", required = true) String fileType,
                                     @RequestParam(value = "localDate", required = true) String localDate) {
        return secSpaceTimeService.getSvtecData(fileType, localDate);
    }

    /**
     * 上传电离层文件
     *
     * @param fileType  文件类型
     * @param filePath  文件路径
     * @param localDate 文件全路径
     * @return STEC、VTEC数据集合
     */
    @ApiOperation("上传AP/KP/F10.7/电离层数据")
    @PostMapping("ionoData")
    public String uploadIonoData(
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "filePath", required = true) String filePath,
            @RequestParam(value = "localDate", required = true) String localDate) {
        return secSpaceTimeService.uploadData(fileType, filePath, localDate);
    }
}
