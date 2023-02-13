package cn.piesat.sec.consumer;

import cn.piesat.sec.comm.constant.KafkaConstant;
import cn.piesat.sec.comm.properties.SecMinioProperties;
import cn.piesat.sec.comm.util.DateUtil;
import cn.piesat.sec.comm.util.MinioUtil;
import cn.piesat.sec.model.vo.SecSpaceFileVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private SecMinioProperties secMinioProperties;

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
                    minioUtil.download(secMinioProperties.getBucketName(), path, "testOut");
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
}
