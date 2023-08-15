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
    private String tecChinaPy;

    /**
     * TEC全球站点三维出图算法脚本
     */
    private String tecGlobalPy;

    /**
     * ROTI全球站点三维出图算法脚本
     */
    private String rotiGlobalPy;

    /**
     * TEC全国站点出图位置
     */
    private String tecChina;

    /**
     * TEC 全球出图位置
     */
    private String tecGlobal;

    /**
     * 4s闪烁指数
     */
    private String s4FlickerIndexPy;

    /**
     * S4闪烁指数数据
     */
    private String s4DataPath;

    /**
     * ROTI出图位置
     */
    private String rotiPics;

    /**
     * roti算法卫星数据
     */
    private String rotiSatelliteData;

    /**
     * roti 星历数据
     */
    private String rotiEphemerisData;

    /**
     * tec 解析数据
     */
    private String parseTectoDBpy;
}
