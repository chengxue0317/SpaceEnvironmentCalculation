package cn.piesat.sec.comm.util;

import cn.piesat.sec.comm.constant.Constant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * 压缩文件到zip同级目录
     * 所有文件夹下的文件都压缩到同一个文件夹目录，因此不能含有同名文件
     *
     * @param input  要压缩的文件/文件夹路径
     * @param output 输出压缩后文件路径
     */
    public static void files2zip(String input, String output) {
        if (!FileUtils.getFile(input).exists()) {
            logger.warn(String.format(Locale.ROOT, "No files to compress!"));
            return;
        }
        makeParentDir(output);
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output))) {
            List<String> filePaths = FileUtil.getFilePaths(input);
            if (CollectionUtils.isEmpty(filePaths)) {
                logger.warn(String.format(Locale.ROOT, "No files to compress!"));
                return;
            }
            for (String path : filePaths) {
                writeEntry2zip(out, FileUtils.getFile(path), null);
            }
        } catch (FileNotFoundException e) {
            logger.error(String.format(Locale.ROOT, "Compressed zip file throws FileNotFoundException: %s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Compressed zip file throws IOException: %s", e.getMessage()));
        }
    }

    private static void makeParentDir(String output) {
        FileUtil.mkdirs(FileUtils.getFile(output).getParentFile().getAbsolutePath());
    }

    /**
     * 递归保留文件目录压缩文件到zip
     *
     * @param input  要压缩的文件/文件夹路径
     * @param output 输出压缩后文件路径
     */
    public static void files2zipc(String input, String output) {
        if (!FileUtils.getFile(input).exists()) {
            logger.warn(String.format(Locale.ROOT, "No files to compress!"));
            return;
        }
        makeParentDir(output);
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output))) {
            File file = FileUtils.getFile(input);
            if (file.isFile()) {
                writeEntry2zip(out, file, null);
            } else if (file.isDirectory()) {
                files2zip(out, file, file.getName());
            } else {
                logger.error(String.format(Locale.ROOT, ""));
            }
        } catch (FileNotFoundException e) {
            logger.error(String.format(Locale.ROOT, "Compressed zip file throws FileNotFoundException-- %s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Compressed zip file throws IOException-- %s", e.getMessage()));
        }
    }

    /**
     * 将单个文件实例写到zip压缩文件流
     *
     * @param out  zip压缩文件流
     * @param file 要压缩的文件
     * @param name 文件所在文件夹名称
     */
    private static void writeEntry2zip(ZipOutputStream out, File file, String name) {
        try (InputStream fis = new FileInputStream(file)) {
            name = StringUtils.isEmpty(name) ? "" : name.concat(Constant.FILE_SEPARATOR);
            // 决定写出文件是否带有父级文件夹
            out.putNextEntry(new ZipEntry(name.concat(file.getName())));
            int len;
            byte[] buffer = new byte[Constant.BUFFSIZE];
            while ((len = fis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.closeEntry();
        } catch (FileNotFoundException e) {
            logger.error(String.format(Locale.ROOT, "Compressed zip file throws FileNotFoundException--- %s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Compressed zip file throws IOException--- %s", e.getMessage()));
        }
    }

    /**
     * 递归写单个文件到zip文件流中
     *
     * @param out     压缩文件流
     * @param file    文件
     * @param pkgName 文件所在文件夹相对路径
     */
    private static void files2zip(ZipOutputStream out, File file, String pkgName) {
        File[] files = file.listFiles();
        // 保留空文件夹
        if (files.length == 0) {
            try {
                out.putNextEntry(new ZipEntry(pkgName.concat(Constant.FILE_SEPARATOR)));
                out.flush();
                out.closeEntry();
            } catch (IOException e) {
                logger.error(String.format(Locale.ROOT, "", e.getMessage()));
            }
        }
        for (File fitem : files) {
            if (fitem.isFile()) {
                writeEntry2zip(out, fitem, pkgName);
            } else if (fitem.isDirectory()) {
                files2zip(out, fitem, pkgName.concat(Constant.FILE_SEPARATOR).concat(fitem.getName()));
            } else {
                continue;
            }
        }
    }

    /**
     * 解压zip文件
     * 将zip文件解压到指定文件路径下
     *
     * @param zipPath zip压缩文件
     * @param tarpkg  解压目标文件夹
     */
    public static void zip2files(String zipPath, String tarpkg) {
        if (StringUtils.isEmpty(zipPath) || !FileUtils.getFile(zipPath).exists()) {
            logger.error(String.format(Locale.ROOT, "No files to umcompress!"));
            return;
        }
        // 文件解压路径，如果不存在则创建
        FileUtil.mkdirs(tarpkg);
        try (ZipFile zipfile = new ZipFile(zipPath, Charset.forName(Constant.GBK));){
            Enumeration<? extends ZipEntry> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                String entryName = zipEntry.getName();
                String outPath = tarpkg.concat(Constant.FILE_SEPARATOR).concat(entryName);
                FileUtil.mkdirs(FileUtils.getFile(outPath).getParentFile().getPath());
                if (entryName.endsWith("\\") || entryName.endsWith("/")) {
                    FileUtil.mkdirs(outPath);
                    continue;
                }
                writeFile(zipfile, zipEntry, outPath);
            }
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Unzipping zip file throws IOException. --- %s", e.getMessage()));
        }
    }

    /**
     * 写出文件
     *
     * @param zipfile  zip文件
     * @param zipEntry zip文件对象
     * @param outPath  写出文件路径
     */
    private static void writeFile(ZipFile zipfile, ZipEntry zipEntry, String outPath) {
        try (InputStream ins = zipfile.getInputStream(zipEntry); OutputStream ous = new FileOutputStream(outPath)) {
            byte[] buff = new byte[Constant.BUFFSIZE];
            int len;
            while ((len = ins.read(buff)) != -1) {
                ous.write(buff, 0, len);
            }
            ous.flush();
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Unzipping zip file throws IOException. --- %s", e.getMessage()));
        }
    }
}
