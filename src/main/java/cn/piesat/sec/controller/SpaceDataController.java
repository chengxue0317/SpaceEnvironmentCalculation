package cn.piesat.sec.controller;

import cn.piesat.sec.comm.kafka.KafkaSendServiceImpl;
import cn.piesat.sec.comm.oss.OSSInstance;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "共性数据入库接口测试")
@RestController
@RequestMapping("/spacedata")
@RequiredArgsConstructor
public class SpaceDataController {
    @Value("${s3.bucketName}")
    private String bucketName;

    @Autowired
    private KafkaSendServiceImpl kafkaSendService;

    @ApiOperation("空间环境数据更新并上传数据发送数据文件消息")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "key", value = "数据关键字", dataType = "String", required = false),
            @ApiImplicitParam(name = "filename", value = "文件名称", dataType = "String", required = false),
            @ApiImplicitParam(name = "dataFlag", value = "数据标识", dataType = "String", required = true),
            @ApiImplicitParam(name = "topic", value = "消息主题", dataType = "String", required = true),
            @ApiImplicitParam(name = "file", value = "数据文件", dataType = "MultipartFile", required = true)
    })
    @PostMapping("datatest")
    public Boolean senMessage(@RequestParam(value = "key", required = false) String key,
                              @RequestParam(value = "filename", required = false) String filename,
                              @RequestParam(value = "dataFlag", required = true) String dataFlag,
                              @RequestParam(value = "topic", required = true) String topic,
                              @RequestPart(value = "file", required = true) MultipartFile file) {
        key = OSSInstance.getOSSUtil().upload(bucketName, file);
        filename = file.getOriginalFilename();
        Map<String, String> map = new HashMap<>();
        map.put("bucketName", bucketName);
        map.put("key", key);
        map.put("filename", filename);
        map.put("dataFlag", dataFlag);
        return kafkaSendService.send(topic, JSON.toJSONString(map));
    }
}
