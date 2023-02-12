package cn.piesat.sec.comm.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @description KafkaSendServiceImpl <br>
 * @date 2023-01-05 19:26:35 <br>
 * @author hezhanfeng <br>
 * @version 1.0.0 <br>
 */
@Slf4j
@Component
public class KafkaSendServiceImpl {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    public Boolean send(String topicName, String msgKey, String msgBody) {
        kafkaTemplate.send(topicName, msgKey, msgBody);
        return Boolean.TRUE;
    }


    public Boolean send(String topicName, String msgBody) {
        kafkaTemplate.send(topicName, msgBody);
        return Boolean.TRUE;
    }

    public Boolean sendCallback(String topic, String msgKey, String jsonStr) {
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, msgKey, jsonStr);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.debug(topic + " 生产者 发送消息成功：" + result.toString());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.info(topic + " 生产者 发送消息失败：" + ex.getMessage());
            }
        });
        return Boolean.TRUE;
    }
}
