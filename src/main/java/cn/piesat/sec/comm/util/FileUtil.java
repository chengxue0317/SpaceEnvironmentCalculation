package cn.piesat.sec.comm.util;

import cn.piesat.sec.comm.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * 文件工具类
 */
@Api("文件工具类")
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    /**
     * 今天文件路径
     *
     * @return
     */
    @ApiOperation("获得某天日期的文件路径")
    public static String getAdayFilePath(int n) {
        String toDay = DateUtil.getDaysLater(n);
        String year = toDay.substring(0, 4);
        String month = toDay.substring(5, 7);
        String day = toDay.substring(8, 10);
        return year + Constant.FILE_SEPARATOR + month + Constant.FILE_SEPARATOR + day + Constant.FILE_SEPARATOR;
    }

    /**
     * 创建文件夹目录
     *
     * @param path 文件夹路径
     */
    @ApiOperation("创建文件夹目录")
    public static void mkdirs(String path) {
        File file = FileUtils.getFile(path);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                logger.error("-----Failed to create a folder!  %s", path);
            }
        }
    }

    /**
     * 删除文件夹下的所有文件
     *
     * @param isDelRootFile 是否删除根文件夹下的文件
     * @param dir           文件/文件夹路径
     */
    @ApiOperation("删除文件夹下的所有文件")
    public static void delDirFiles(File dir, boolean isDelRootFile) {
        File file = FileUtils.getFile(dir);
        if (file.isDirectory()) {
            File[] fls = file.listFiles();
            for (File fl : fls) {
                delDirFiles(fl, true);
            }
            delFile(isDelRootFile, file);
        } else {
            delFile(isDelRootFile, file);
        }
    }

    private static void delFile(boolean isDelRootFile, File file) {
        if (isDelRootFile) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                logger.error(String.format(Locale.ROOT, "-------Deleting a file %s throws an exception %s", file.getAbsolutePath(), e.getMessage()));
            }
        }
    }
}
