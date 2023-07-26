package cn.piesat.sec.model.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 共性服务消息对象
 */
@Data
public class SecIISVO {
    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 文件路径
     */
    private String key;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 数据标记
     */
    private String dataFlag;
}
