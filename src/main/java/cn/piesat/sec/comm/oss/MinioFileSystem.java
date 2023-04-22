package cn.piesat.sec.comm.oss;

import cn.piesat.sec.comm.constant.Constant;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Component
@Slf4j
public class MinioFileSystem implements IFileSystem {
    private static final Logger logger = LoggerFactory.getLogger(MinioFileSystem.class);

    @Autowired(required = false)
    private MinioClient minioClient;

    /**
     * 查看存储bucket是否存在
     *
     * @return boolean
     */
    public Boolean bucketExists(String bucketName) {
        boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===bucketExists %s", e.getMessage()));
            return false;
        }
        return found;
    }

    /**
     * 创建存储bucket
     */
    public void makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===makeBucket %s", e.getMessage()));
        }
    }

    /**
     * 删除存储bucket
     *
     * @return Boolean
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===removeBucket %s", e.getMessage()));
            return false;
        }
        return true;
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===getAllBuckets %s", e.getMessage()));
        }
        return null;
    }

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @return
     */
    public String upload(String bucketName, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException();
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formatDatePath = formatter.format(now);
        // 加一个时间戳
        long timeMillis = System.currentTimeMillis();
        String objectName = formatDatePath + "/" + timeMillis + fileName;
        try {
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===upload %s", e.getMessage()));
            return null;
        }
        return objectName;
    }

    /**
     * 上传文件
     *
     * @param bucketName 文件存储桶
     * @param objectName 上传文件保存到minio的全路径
     * @param fileName   本地文件路径
     * @return 上传文件路径
     */
    public String upload(String bucketName, String objectName, String fileName) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)        // 桶名称
                            .object(objectName)        // 上传文件保存到minio的全路径
                            .filename(fileName)        // 本地文件路径
                            .build());
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===upload %s", e.getMessage()));
            return null;
        }
        return objectName;
    }

    /**
     * 预览图片
     *
     * @param bucketName 文件存储桶
     * @param fileName   文件名称
     * @return 图片地址
     */
    public String preview(String bucketName, String fileName) {
        // 查看文件地址
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .method(Method.GET)
                .build();
        try {
            return minioClient.getPresignedObjectUrl(build);
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===preview %s", e.getMessage()));
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param bucketName 文件存储桶
     * @param fileName   文件名称
     * @param tarDir     目标文件夹
     */
    public void download(String bucketName, String fileName, String tarDir) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                try (FileOutputStream stream = FileUtils.openOutputStream(FileUtils.getFile("/" + tarDir + fileName), true)) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===download %s", e.getMessage()));
        }
    }

    /**
     * 文件下载
     *
     * @param bucketName 文件存储桶
     * @param fileName   文件名称
     * @param res        response
     */
    public void download(String bucketName, String fileName, HttpServletResponse res) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                .object(fileName).build();
        try (GetObjectResponse response = minioClient.getObject(objectArgs)) {
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = response.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                res.setCharacterEncoding("utf-8");
                // 设置强制下载不打开
                res.setContentType("application/force-download");
                res.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                try (ServletOutputStream stream = res.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===download %s", e.getMessage()));
        }
    }

    /**
     * 文件下载
     *
     * @param bucketName 数据桶名称
     * @param dirpath    文件路径
     * @param response   浏览器响应对象
     * @param recursive  是否递归
     */
    public void download(String bucketName, String dirpath, HttpServletResponse response, boolean recursive) {
        List<Item> items = listObjects(bucketName, dirpath, recursive);
        ZipOutputStream zout = null;
        try {
            response.setCharacterEncoding(Constant.UTF8);
            response.setContentType("multipart/form-data;application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(String.valueOf(System.currentTimeMillis()), "UTF-8"));
            zout = new ZipOutputStream(response.getOutputStream());
            if (CollectionUtils.isNotEmpty(items)) {
                byte[] buff = new byte[Constant.BUFFSIZE];
                int len;
                for (Item item : items) {
                    GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                            .object(item.objectName()).build();
                    GetObjectResponse objresp = minioClient.getObject(objectArgs);
                    zout.putNextEntry(new ZipEntry(item.objectName()));
                    while ((len = objresp.read(buff)) != -1) {
                        zout.write(buff, 0, len);
                    }
                    zout.flush();
                    zout.closeEntry();
                    objresp.close();
                }
                zout.flush();
                zout.finish();
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "========File download exception %s", e.getMessage()));
        } finally {
            if (null != zout) {
                try {
                    zout.close();
                } catch (IOException e) {
                    logger.error("--------Failed to close ZipoutputStream. %s", e.getMessage());
                }
            }
            return;
        }
    }

    @Override
    public void download(String bucketName, List<String> pathList, HttpServletResponse response) {
        ZipOutputStream zout = null;
        try {
            response.setCharacterEncoding(Constant.UTF8);
            response.setContentType("multipart/form-data;application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(String.valueOf(System.currentTimeMillis()), "UTF-8"));
            zout = new ZipOutputStream(response.getOutputStream());
            if (CollectionUtils.isNotEmpty(pathList)) {
                byte[] buff = new byte[Constant.BUFFSIZE];
                int len;
                for (String path : pathList) {
                    GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName)
                            .object(path).build();
                    if (null != objectArgs) {
                        GetObjectResponse objresp = minioClient.getObject(objectArgs);
                        zout.putNextEntry(new ZipEntry(path));
                        while ((len = objresp.read(buff)) != -1) {
                            zout.write(buff, 0, len);
                        }
                        zout.flush();
                        zout.closeEntry();
                        objresp.close();
                    }
                }
                zout.flush();
                zout.finish();
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "========File download exception %s", e.getMessage()));
        } finally {
            if (null != zout) {
                try {
                    zout.close();
                } catch (IOException e) {
                    logger.error("--------Failed to close ZipoutputStream. %s", e.getMessage());
                }
            }
            return;
        }
    }

    /**
     * 获取路径下文件列表
     *
     * @param bucketName bucket名称
     * @param prefix     文件名称
     * @param recursive  是否递归查找，如果是false,就模拟文件夹结构查找
     * @return 二进制流
     */
    public List<Item> listObjects(String bucketName, String prefix,
                                  boolean recursive) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(recursive).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===listObjects %s", e.getMessage()));
            return null;
        }
        return items;
    }


    /**
     * 查看文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects(String bucketName) {
        if (StringUtils.isBlank(bucketName) || !bucketExists(bucketName)) {
            return null;
        }
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).recursive(true).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===listObjects %s", e.getMessage()));
            return null;
        }
        return items;
    }

    /**
     * 查看文件对象
     *
     * @return 存储bucket内文件对象信息
     */
    public List<Item> listObjects(String bucketName, String startAf) {
        if (StringUtils.isBlank(bucketName) || !bucketExists(bucketName)) {
            return null;
        }
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).startAfter(startAf).recursive(true).build());
        List<Item> items = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                items.add(result.get());
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===listObjects %s", e.getMessage()));
            return null;
        }
        return items;
    }

    /**
     * 判断文件是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 对象
     * @return true：存在
     */
    public boolean doesObjectExist(String bucketName, String objectName) {
        boolean exist = true;
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    /**
     * 获取单个桶中的所有文件对象名称
     *
     * @param bucket 桶名称
     * @return {@link List}<{@link String}>
     */
    public List<String> getBucketObjectName(String bucket) {
        boolean exsit = bucketExists(bucket);
        if (exsit) {
            List<String> listObjetcName = new ArrayList<>();
            try {
                Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).build());
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    listObjetcName.add(item.objectName());
                }
                return listObjetcName;
            } catch (Exception e) {
                logger.error(String.format(Locale.ROOT, "===getBucketObjectName %s", e.getMessage()));
            }
        }
        return null;
    }

    /**
     * 删除
     *
     * @param bucketName 文件存储桶
     * @param fileName   文件名称
     * @return true|false
     */
    public boolean remove(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 批量删除文件
     *
     * @param bucketName  桶名称
     * @param objectNames 对象名称
     * @return boolean
     */
    public boolean removeObjects(String bucketName, List<String> objectNames) {
        boolean exsit = bucketExists(bucketName);
        if (exsit) {
            try {
                List<DeleteObject> objects = new LinkedList<>();
                for (String str : objectNames) {
                    objects.add(new DeleteObject(str));
                }
                minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
                return true;
            } catch (Exception e) {
                logger.error(String.format(Locale.ROOT, "===removeObjects %s", e.getMessage()));
            }
        }
        return false;
    }

}
