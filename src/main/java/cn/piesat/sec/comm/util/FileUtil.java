package cn.piesat.sec.comm.util;

import cn.piesat.sec.comm.constant.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * 获取文件/文件夹路径下的所有文件路径
     *
     * @param source 数据源路径
     * @return 文件路径集合
     */
    @ApiOperation("获取文件/文件夹路径下的所有文件路径")
    public static List<String> getFilePaths(String source) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isEmpty(source)) {
            return list;
        }
        if (org.apache.commons.io.FileUtils.getFile(source).isFile()) {
            list.add(source);
        } else if (org.apache.commons.io.FileUtils.getFile(source).isDirectory()) {
            File[] files = org.apache.commons.io.FileUtils.getFile(source).listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    list.add(files[i].getAbsolutePath());
                } else if (files[i].isDirectory()) {
                    list.addAll(getFilePaths(files[i].getAbsolutePath()));
                } else {
                    continue;
                }
            }
        } else {
            return list;
        }
        return list;
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

    /**
     * 删除路径前分隔符
     *
     * @param path 文件路径
     * @return 去除前缀
     */
    public static String rmPathPreSplit(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        } else {
            if (path.indexOf("/") == 0) {
                path = path.replaceFirst("/", "");
            }
            return path;
        }
    }

    /**
     * 读取txt文件数据
     *
     * @param path
     * @return
     */
    public static List<String> readTxtFile2List(String path) {
        List<String> list = new ArrayList<>();
        try (FileInputStream inputStream = FileUtils.openInputStream(FileUtils.getFile(path));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Constant.UTF8))) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (StringUtils.isEmpty(str)) {
                    continue;
                } else {
                    list.add(StringUtils.trim(str));
                }
            }
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "-------readTxtFile2List %s throws an exception %s", path, e.getMessage()));
        }
        return list;
    }

    /**
     * 路径校验
     *
     * @param filePath 文件路径
     * @return 返回路径
     */
    public static String checkPath(String filePath) {
        return filePath.replaceAll("//", "/").replaceAll("[\\\\]+", "/");
    }

}
