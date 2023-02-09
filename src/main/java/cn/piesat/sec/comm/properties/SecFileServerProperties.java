package cn.piesat.sec.comm.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 读取项目相关配置
 *
 * @author piesat
 */
@Getter
@Setter
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "piesat")
public class SecFileServerProperties {
    /**
     * 文件路径
     */
    private String profile;

    /**
     * 周报算法脚本相对位置
     */
    private String weekPngPy;

    /**
     * 周报算法脚本数据库配置相对位置
     */
    private String weekPngIni;

    /**
     * TEC全国站点出图算法脚本
     */
    private String tecStationPy;

    /**
     * TEC全球站点三维出图算法脚本
     */
    private String tecGLStationPy;

    /**
     * ROTI全球站点三维出图算法脚本
     */
    private String rotiGLStationPy;

    /**
     * TEC全国站点出图位置
     */
    private String tecStations;

    /**
     * TEC 时段出图位置
     */
    private String tecTimes;

    /**
     * 电离层闪烁 全国站点出图位置
     */
    private String s4Stations;

    /**
     * 电离层闪烁 时段出图位置
     */
    private String s4Times;

    /**
     * ROTI出图位置
     */
    private String roti;

    /**
     * 算法二级目录
     */
    private String secondDir;
}
