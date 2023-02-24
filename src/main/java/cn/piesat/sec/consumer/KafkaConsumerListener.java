package cn.piesat.sec.consumer;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.KafkaConstant;
import cn.piesat.sec.comm.oss.OSSInstance;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.model.vo.SecIISVO;
import cn.piesat.sec.model.vo.SecSpaceFileVO;
import cn.piesat.sec.service.impl.dataparse.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hezhanfeng <br>
 * @version v1.0 <br>
 * @description KafkaConsumerListener <br>
 * @date 2023-01-07 19:50:56 <br>
 */
@Component
@Slf4j
public class KafkaConsumerListener {
    @Value("${s3.bucketName}")
    private String bucketName;

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


    public KafkaConsumerListener() {
    }

    /**
     * 时空文件更新消息监听
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CMS_STIP_SEG)
    public void cmsStipSegListener(ConsumerRecord<String, String> record) {
        String jsonStr = record.value();
        // TODO 如何处理待定
        System.out.println(DateUtil.getToDay() + "接收到时空文件更新消息:=======  " + jsonStr);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Object fileList = jsonObject.get("fileList");
        if (fileList != null) {
            List<SecSpaceFileVO> filePath = JSON.parseObject(JSON.toJSONString(fileList), new TypeReference<ArrayList<SecSpaceFileVO>>() {
            });
            if (CollectionUtils.isNotEmpty(filePath)) {
                for (SecSpaceFileVO vo : filePath) {
                    String path = vo.getFilePath();
                    OSSInstance.getOSSUtil().download(bucketName, path, "testOut");
                }
            }
        }
    }

    /**
     * 接收空间环境给时空发的tec文件更新消息
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_TEC)
    public void tecListener(ConsumerRecord<String, String> record) {
        String jsonStr = record.value();
        // TODO 如何处理待定
        System.out.println("接收到空间环境TEC文件更新新消息:========  " + jsonStr);
    }

    /**
     * 接收空间环境给时空发的ap\kp\f107文件更新消息
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_SPACE_ENV)
    public void apkpf107Listener(ConsumerRecord<String, String> record) {
        String jsonStr = record.value();
        // TODO 如何处理待定
        System.out.println("接收到空间环境AP/AP/F107文件更新新消息:========  " + jsonStr);
    }

    /**
     * 气象实况数据更新
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_WeatherReal)
    public void weatherReal(ConsumerRecord<String, String> record) {
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/testrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 空间环境数据更新信息
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_UpdateMsg)
    public void updateMsg(ConsumerRecord<String, String> record) {
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/testrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 卫星高能粒子数据
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_HEPartical)
    public int proEle(ConsumerRecord<String, String> record) {
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/HEParticalrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return hePartical.parseData(secIISVO);
    }

    /**
     * 空间环境事件数据
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_Event)
    public int seEvents(ConsumerRecord<String, String> record) {
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/Eventrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return seEventsImpl.parseData(secIISVO);
    }

    /**
     * 太阳F10.7指数数据
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_F107)
    public int soF107(ConsumerRecord<String, String> record) {
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/F107records.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return soF107Impl.parseData(secIISVO);
    }

    /**
     * AP指数数据
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_AP)
    public int gmap(ConsumerRecord<String, String> record) {
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/APrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return gmapImpl.parseData(secIISVO);
    }

    /**
     * KP指数数据
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_KP)
    public int gmkp(ConsumerRecord<String, String> record) {
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/KPrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return gmkpImpl.parseData(secIISVO);
    }

    /**
     *电离层TEC数据
     * @param record
     * @return
     */
    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_TEC)
    public int tonoTec(ConsumerRecord<String,String> record){
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/tecTestrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return ionoTecImpl.parseData(secIISVO);
    }

    /**
     *空间环境每日预报预制备数据
     * @param record
     * @return*/

    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_DayFore)
    public int dayFore(ConsumerRecord<String,String> record){
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/dayForeTestrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return dayForeImpl.parseData(secIISVO);
    }

    /**
     *空间环境周报预制备数据
     * @param record
     * @return*/

    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_WeekFore)
    public int weekFore(ConsumerRecord<String,String> record){
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/weekForeTestrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return weekForeImpl.parseData(secIISVO);
    }

    /**
     *空间环境月报预制备数据
     * @param record
     * @return*/

    @KafkaListener(topics = KafkaConstant.THEME_CSS_IIS_MonthFore)
    public int monthFore(ConsumerRecord<String,String> record){
        try {
            FileUtils.writeStringToFile(FileUtils.getFile("/testOut/mornthForeTestrecords.txt"), LocalDateTime.now() + record.value(), Constant.UTF8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SecIISVO secIISVO = JSON.parseObject(record.value(), SecIISVO.class);
        return monthForeImpl.parseData(secIISVO);
    }
}
