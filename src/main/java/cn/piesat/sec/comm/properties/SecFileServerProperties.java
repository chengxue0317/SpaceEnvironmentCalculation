package cn.piesat.sec.comm.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author piesat
 */
@Component
@ConfigurationProperties(prefix = "piesat")
public class SecFileServerProperties {
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

    /**
     * TEC全国站点出图位置
     */
    private static String tecStations;

    /**
     * TEC 时段出图位置
     */
    private static String tecTimes;

    /**
     * 电离层闪烁 全国站点出图位置
     */
    private static String s4Stations;

    /**
     * 电离层闪烁 时段出图位置
     */
    private static String s4Times;

    /**
     * ROTI出图位置
     */
    private static String roti;

    /**
     * 算法二级目录
     */
    private static String secondDir;

    public static String getSecondDir() {
        return secondDir;
    }

    public void setSecondDir(String secondDir) {
        SecFileServerProperties.secondDir = secondDir;
    }

    public static String getRoti() {
        return roti;
    }

    public void setRoti(String roti) {
        SecFileServerProperties.roti = roti;
    }

    public static String getS4Stations() {
        return s4Stations;
    }

    public void setS4Stations(String s4Stations) {
        SecFileServerProperties.s4Stations = s4Stations;
    }

    public static String getS4Times() {
        return s4Times;
    }

    public void setS4Times(String s4Times) {
        SecFileServerProperties.s4Times = s4Times;
    }

    public static String getTecStations() {
        return tecStations;
    }

    public void setTecStations(String tecStations) {
        SecFileServerProperties.tecStations = tecStations;
    }

    public static String getTecTimes() {
        return tecTimes;
    }

    public void setTecTimes(String tecTimes) {
        SecFileServerProperties.tecTimes = tecTimes;
    }

    public static String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        SecFileServerProperties.ip = ip;
    }

    public static String getPort() {
        return port;
    }

    public void setPort(String port) {
        SecFileServerProperties.port = port;
    }

    public static String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        SecFileServerProperties.profile = profile;
    }

}
