package cn.piesat.sec.comm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 命令工具类
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
public class ProcessUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProcessUtil.class);

    /**
     * 转换java程序可执行命令
     *
     * @param command 命令
     * @return 可执行命令
     */
    public static String[] getCommand(String command) {
        String os = System.getProperty("os.name");
        String shell = "/bin/bash";
        String c = "-c";
        if (os.toLowerCase().startsWith("win")) {
            shell = "cmd";
            c = "/c";
        }
        String[] cmd = {shell, c, command};
        logger.info("-------The executable command is ：" + Arrays.toString(cmd));
        return cmd;
    }
}
