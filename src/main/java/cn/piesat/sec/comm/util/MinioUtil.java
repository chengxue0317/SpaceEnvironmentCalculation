package cn.piesat.sec.comm.util;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
@Slf4j
public class MinioUtil {
    private static final Logger logger = LoggerFactory.getLogger(MinioUtil.class);

    @Resource
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
     * @param objectName 文件在文件服务器上的相对路径
     * @param fileName   上传文件全路径
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
                // res.setContentType("application/force-download");
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
