package cn.piesat.sec.consumer;

import cn.piesat.sec.comm.constant.KafkaConstant;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author hezhanfeng <br>
 * @version v1.0 <br>
 * @description KafkaConsumerListener <br>
 * @date 2023-01-07 19:50:56 <br>
 */
@Component
@Slf4j
public class KafkaConsumerListener {
    /**
     * 时空文件更新消息监听
     *
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.THEME_CMS_STIP_SEG)
    public void cmsStipSegListener(ConsumerRecord<String, String> record) {
        String jsonStr = record.value();
        // TODO 如何处理待定
        System.out.println("接收到时空文件更新消息:=======  " + jsonStr);
    }

//    /**
//     * 接收空间环境给时空发的tec文件更新消息
//     *
//     * @param record
//     */
//    @KafkaListener(topics = KafkaConstant.THEME_TEC)
//    public void tecListener(ConsumerRecord<String, String> record) {
//        String jsonStr = record.value();
//        // TODO 如何处理待定
//        System.out.println("testListener:========  " + jsonStr);
//    }
//
//    /**
//     * 接收空间环境给时空发的ap\kp\f107文件更新消息
//     *
//     * @param record
//     */
//    @KafkaListener(topics = KafkaConstant.THEME_SPACE_ENV)
//    public void apkpf107Listener(ConsumerRecord<String, String> record) {
//        String jsonStr = record.value();
//        // TODO 如何处理待定
//        System.out.println("testListener:========  " + jsonStr);
//    }

}
