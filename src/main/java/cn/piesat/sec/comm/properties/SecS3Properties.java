package cn.piesat.sec.comm.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 读取minio相关配置
 *
 * @author piesat
 */
@Getter
@Setter
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "s3")
public class SecS3Properties {
    /**
     * 文件路径
     */
    private  String endpoint;

    /**
     * 文件服务IP
     */
    private  String accesskey;

    /**
     * 文件服务端口
     */
    private  String secretkey;

    /**
     * TEC全国站点出图位置
     */
    private  String bucketName;
}
