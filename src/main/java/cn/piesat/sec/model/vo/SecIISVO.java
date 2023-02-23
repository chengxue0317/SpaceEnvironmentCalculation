package cn.piesat.sec.model.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 共性服务消息对象
 */
@Data
@Getter
@Setter
public class SecIISVO {
    private String bucketName;
    private String key;
    private String filename;
    private String dataFlag;
}
