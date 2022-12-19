package cn.piesat.sec.comm.constant;

import java.util.*;

/**
 * 空间环境时空接口对应常量类
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
public class SpaceTimeConst {
    /**
     * 数据类型对应文件名称
     */
    public static final Map<String, String> FILE_TYPE_NAME = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put("f107", "SoF107");
            put("ap", "GmAp");
            put("kp", "GmKp");
            put("tec", "IonoTec");
        }
    });

    /**
     * 数据类型对应文件名称
     */
    public static final Map<String, String> FILE_TYPE_TABLE = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put("f107", "SEC_F107_FLUX");
            put("ap", "SEC_AP_INDEX");
            put("kp", "SEC_KP_INDEX");
            put("tec", "SEC_IONOSPHERIC_TEC");
        }
    });

    /**
     * 数据类型对应文件字段名称
     */
    public static final Map<String, List<String>> FILE_TYPE_HEAD = Collections.unmodifiableMap(new HashMap<String, List<String>>() {
        {
            put("f107", Arrays.asList("TIME", "F107"));
            put("ap", Arrays.asList("TIME", "AP"));
            put("kp", Arrays.asList("TIME", "KP1", "KP2", "KP3", "KP4", "KP5", "KP6", "KP7", "KP8"));
            put("tec", Arrays.asList("TIME", "VTEC"));
        }
    });
}
