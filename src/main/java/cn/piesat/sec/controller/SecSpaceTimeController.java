package cn.piesat.sec.controller;

import cn.piesat.sec.comm.util.MinioUtil;
import cn.piesat.sec.model.vo.SecSpaceTimeVO;
import cn.piesat.sec.service.SecSpaceTimeService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
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
@Api(tags = "空间环境给时空接口")
@RestController
@RequestMapping("/spacetime")
@RequiredArgsConstructor
public class SecSpaceTimeController {
    private final SecSpaceTimeService secSpaceTimeService;
    @Resource
    MinioUtil minioUtil;


    @GetMapping("fileinfo")
    public SecSpaceTimeVO getSpaceTimeFileInfo(@RequestParam(value = "fileType", required = true) String fileType,
                                               @RequestParam(value = "startTime", required = true) String startTime,
                                               @RequestParam(value = "endTime", required = true) String endTime) {
        return secSpaceTimeService.getSpaceTimeFileInfo(fileType, startTime, endTime);
    }

    @GetMapping("/fileInformation")
    public SecSpaceTimeVO getFileInformation(@RequestParam("fileType") String fileType,
                                             @RequestParam("startTime") String startTime,
                                             @RequestParam("endTime") String endTime) {
        return secSpaceTimeService.getFileInformation(fileType, startTime, endTime);
    }

    @GetMapping("/list")
    public List<Object> list() {
        return secSpaceTimeService.list();
    }

    @PostMapping("/uploadFiles")
    public boolean uploadFiles(@RequestParam("filesPath") List<String> filesPath) {
        return secSpaceTimeService.uploadFiles(filesPath);
    }


    @PostMapping("/downloadFile")
    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        minioUtil.download(fileName, response);
    }
}
