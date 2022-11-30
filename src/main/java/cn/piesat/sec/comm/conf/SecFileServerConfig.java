package cn.piesat.sec.comm.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author piesat
 */
@Component
@ConfigurationProperties(prefix = "piesat")
public class SecFileServerConfig {
    /**
     * 文件路径
     */
    private static String profile;

    /**
     * 文件服务IP
     */
    private static String ip;

    /**
     * 文件服务端口
     */
    private static String port;

    public static String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        SecFileServerConfig.ip = ip;
    }

    public static String getPort() {
        return port;
    }

    public void setPort(String port) {
        SecFileServerConfig.port = port;
    }

    public static String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        SecFileServerConfig.profile = profile;
    }

}
