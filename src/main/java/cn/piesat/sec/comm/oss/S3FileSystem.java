package cn.piesat.sec.comm.oss;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.properties.SecS3Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Component
@Slf4j
public class S3FileSystem implements IFileSystem {
    private static final Logger logger = LoggerFactory.getLogger(S3FileSystem.class);

    @Autowired(required = false)
    private S3Client s3Client;

    @Autowired(required = false)
    private SecS3Properties secS3Properties;

    /**
     * 查看存储bucket是否存在
     *
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    public Boolean bucketExists(String bucketName) {
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }

    /**
     * 创建存储bucket
     *
     * @param bucketName 桶名称
     */
    public void makeBucket(String bucketName) {
        try {
            S3Waiter s3Waiter = s3Client.waiter();
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(bucketRequest);
            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(parm -> {
                logger.info("Make bucket success=========");
            });
        } catch (S3Exception e) {
            logger.error(String.format(Locale.ROOT, "===makeBucket %s", e.getMessage()));
        }
    }

    /**
     * 文件上传
     *
     * @param bucketName 桶名称
     * @param file       上传的文件
     * @return
     */
    public String upload(String bucketName, MultipartFile file) {
        final String originalFilename = file.getOriginalFilename();
        PutObjectRequest putOb = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(originalFilename)
                .build();
        try {
            s3Client.putObject(putOb, RequestBody.fromBytes(file.getBytes()));
            return originalFilename;
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "===Failed to upload file %s", e.getMessage()));
            return null;
        }
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
        objectName = objectName.replaceAll("//", "/");
        fileName = fileName.replaceAll("//", "/");
        objectName = checkPath(objectName);
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

            s3Client.putObject(putOb, RequestBody.fromBytes(getObjectFile(fileName)));
            return objectName;

        } catch (S3Exception e) {
            logger.error(String.format(Locale.ROOT, "===Failed to upload file %s", e.getMessage()));
            return null;
        }
    }

    /**
     * 文件预览
     *
     * @param bucketName 桶名称
     * @param fileName   文件路径
     * @return
     */
    public String preview(String bucketName, String fileName) {
        fileName = checkPath(fileName);
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        BufferedImage imgBuf = null;
        try (InputStream is = s3Client.getObject(objectRequest)) {
            imgBuf = ImageIO.read(is);
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "===Failed to preview png %s", e.getMessage()));
        }
        return encodeBase64URL(imgBuf);
    }

    /**
     * 预览图片
     *
     * @param bucketName 文件存储桶
     * @param fileName   文件名称
     * @return 图片地址
     */
    public String preview2(String bucketName, String fileName) {
        fileName = checkPath(fileName);

        S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(secS3Properties.getAccesskey(), secS3Properties.getSecretkey())))
                .region(Region.US_EAST_1)
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        //默认生成的预览url最大时效时间为：7天，aws s3能允许的最大时限也是7天
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        String theUrl = presignedGetObjectRequest.url().toString();
        return theUrl;
    }

    public String encodeBase64URL(BufferedImage imgBuf) {
        String base64 = StringUtils.EMPTY;
        if (imgBuf == null) {
            base64 = null;
        } else {
            Base64 encoder = new Base64();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                ImageIO.write(imgBuf, "PNG", out);
                byte[] bytes = out.toByteArray();
                base64 = "data:image/png;base64," + new String(encoder.encode(bytes), "UTF-8");
            } catch (IOException e) {
                logger.error(String.format(Locale.ROOT, "===Failed to write png %s", e.getMessage()));
            }
        }

        return base64;
    }

    /**
     * 文件下载
     *
     * @param bucketName 文件存储桶
     * @param fileName   文件名称
     * @param tarDir     目标文件夹
     */
    public void download(String bucketName, String fileName, String tarDir) {
        fileName = checkPath(fileName);
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        byte[] buf = new byte[1024];
        int len;
        try (InputStream is = s3Client.getObject(objectRequest);
             FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.flush();
            byte[] bytes = os.toByteArray();
            String outPath = "/" + tarDir + "/" + fileName;
            outPath = outPath.replaceAll("//", "/");
            try (FileOutputStream stream = FileUtils.openOutputStream(FileUtils.getFile(outPath), true)) {
                stream.write(bytes);
                stream.flush();
            }
        } catch (IOException e) {
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
        fileName = checkPath(fileName);
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        byte[] buf = new byte[1024];
        int len;
        try (InputStream is = s3Client.getObject(objectRequest);
             FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
            while ((len = is.read(buf)) != -1) {
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
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "===download %s", e.getMessage()));
        }
    }

    @NotNull
    private String checkPath(String fileName) {
        if (fileName.indexOf("/") == 0) {
            fileName = fileName.replaceFirst("/", "");
        }
        return fileName;
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
        List<S3Object> items = listObjects(bucketName, dirpath);
        ZipOutputStream zout = null;
        try {
            response.setCharacterEncoding(Constant.UTF8);
            response.setContentType("multipart/form-data;application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(System.currentTimeMillis() + ".zip", "UTF-8"));
            zout = new ZipOutputStream(response.getOutputStream());
            if (CollectionUtils.isNotEmpty(items)) {
                byte[] buff = new byte[Constant.BUFFSIZE];
                int len;
                for (S3Object item : items) {
                    String key = item.key();
                    key = checkPath(key);
                    GetObjectRequest objectRequest = GetObjectRequest
                            .builder()
                            .bucket(bucketName)
                            .key(key)
                            .build();
                    InputStream is = s3Client.getObject(objectRequest);
                    zout.putNextEntry(new ZipEntry(key));
                    while ((len = is.read(buff)) != -1) {
                        zout.write(buff, 0, len);
                    }
                    zout.flush();
                    zout.closeEntry();
                    is.close();
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
     * 文件下载
     *
     * @param bucketName 数据桶名称
     * @param filePaths  文件路径
     * @param response   浏览器响应对象
     */
    public void download(String bucketName, List<String> filePaths, HttpServletResponse response) {
        ZipOutputStream zout = null;
        try {
            response.setCharacterEncoding(Constant.UTF8);
            response.setContentType("multipart/form-data;application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(System.currentTimeMillis() + ".zip", "UTF-8"));
            zout = new ZipOutputStream(response.getOutputStream());
            if (CollectionUtils.isNotEmpty(filePaths)) {
                byte[] buff = new byte[Constant.BUFFSIZE];
                int len;
                for (String path : filePaths) {
                    path = checkPath(path);
                    GetObjectRequest objectRequest = GetObjectRequest
                            .builder()
                            .bucket(bucketName)
                            .key(path)
                            .build();
                    if (null != objectRequest) {
                        InputStream is = s3Client.getObject(objectRequest);
                        zout.putNextEntry(new ZipEntry(path));
                        while ((len = is.read(buff)) != -1) {
                            zout.write(buff, 0, len);
                        }
                        zout.flush();
                        zout.closeEntry();
                        is.close();
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
     * 获取路径下所有文件列表
     *
     * @param bucketName bucket名称
     * @param prefix     文件名称
     * @return 二进制流
     */
    public List<S3Object> listObjects(String bucketName, String prefix) {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
        ListObjectsResponse results = s3Client.listObjects(listObjectsRequest);
        if (null != results) {
            List<S3Object> contents = results.contents();
            return contents;
        }
        return null;
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
        objectName = checkPath(objectName);
        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .bucket(bucketName)
                .key(objectName)
                .build();
        try (InputStream is = s3Client.getObject(objectRequest);) {
            if (null == is) {
                exist = false;
            } else {
                exist = true;
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "===doesObjectExist %s", e.getMessage()));
            exist = false;
        } finally {
            return exist;
        }
    }

    private static byte[] getObjectFile(String filePath) {
        File file = FileUtils.getFile(filePath);
        byte[] bytesArray = new byte[(int) file.length()];

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(bytesArray);
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "===getObjectFile %s", e.getMessage()));
        }
        return bytesArray;
    }
}
