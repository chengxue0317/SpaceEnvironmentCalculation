package cn.piesat.sec.controller;

import cn.piesat.sec.comm.kafka.KafkaSendServiceImpl;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.service.SecSpaceTimeService;
import cn.piesat.sec.service.impl.dataparse.*;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "空间环境样例数据测试")
@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class SampleDataController {
    @Value("${s3.bucketName}")
    private String bucketName;

    private final SecSpaceTimeService secSpaceTimeService;

    @Autowired
    private KafkaSendServiceImpl kafkaSendService;

    @Autowired
    private HEParticalImpl hePartical;

    @Autowired
    private SeEventsImpl seEventsImpl;

    @Autowired
    private SoF107Impl soF107Impl;

    @Autowired
    private GmapImpl gmapImpl;

    @Autowired
    private GmkpImpl gmkpImpl;

    @Autowired
    private IonoTecImpl ionoTecImpl;

    @Autowired
    private DayForeImpl dayForeImpl;

    @Autowired
    private WeekForeImpl weekForeImpl;

    @Autowired
    private MonthForeImpl monthForeImpl;

    @ApiOperation("数据测试")
    @GetMapping("datatest")
    public Boolean senMessage(@ApiParam(value = "消息主题") @RequestParam(value = "topic") String topic,
                              @ApiParam(value = "文件路径") @RequestParam(value = "key") String key,
                              @ApiParam(value = "文件名称") @RequestParam(value = "filename", defaultValue = "", required = false) String filename,
                              @ApiParam(value = "数据标记") @RequestParam(value = "dataFlag", defaultValue = "", required = false) String dataFlag) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        secIISVO.setFilename(filename);
        secIISVO.setDataFlag(dataFlag);
        return kafkaSendService.send(topic, JSON.toJSONString(secIISVO));
    }

    @ApiOperation("日报")
    @GetMapping("dayReport")
    public Integer dayReport(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return dayForeImpl.parseData(secIISVO);
    }

    @ApiOperation("周报")
    @GetMapping("weekReport")
    public Integer weekReport(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return weekForeImpl.parseData(secIISVO);
    }

    @ApiOperation("月报")
    @GetMapping("monthReport")
    public Integer monthReport(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return monthForeImpl.parseData(secIISVO);
    }

    @ApiOperation("AP")
    @GetMapping("ap")
    public Integer ap(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return gmapImpl.parseData(secIISVO);
    }

    @ApiOperation("KP")
    @GetMapping("kp")
    public Integer kp(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return gmkpImpl.parseData(secIISVO);
    }

    @ApiOperation("f107")
    @GetMapping("f107")
    public Integer f107(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return soF107Impl.parseData(secIISVO);
    }

    @ApiOperation("proEle")
    @GetMapping("proEle")
    public Integer proEle(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return hePartical.parseData(secIISVO);
    }

    @ApiOperation("event")
    @GetMapping("event")
    public Integer event(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return seEventsImpl.parseData(secIISVO);
    }

    @ApiOperation("TEC")
    @GetMapping("tec")
    public Integer tec(@ApiParam(value = "文件路径") @RequestParam(value = "key") String key) {
        SecIISVO secIISVO = new SecIISVO();
        secIISVO.setBucketName(bucketName);
        secIISVO.setKey(key);
        return ionoTecImpl.parseData(secIISVO);
    }
}
