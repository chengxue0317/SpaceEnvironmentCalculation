package cn.piesat.sec.comm.conf;

import io.swagger.annotations.Api;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author piesat
 */
@Api("系统配置类")
@Component
@ConfigurationProperties(prefix = "piesat")
public class SecConfig {
    /**
     * 文件路径
     */
    private static String profile;

    public static String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        SecConfig.profile = profile;
    }
}
