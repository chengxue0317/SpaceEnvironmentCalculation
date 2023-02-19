package cn.piesat.sec.comm.oss;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface IFileSystem {
    Boolean bucketExists(String bucketName);

    void makeBucket(String bucketName);

    String upload(String bucketName, MultipartFile file);

    String upload(String bucketName, String objectName, String fileName);

    String preview(String bucketName, String fileName);

    void download(String bucketName, String fileName, String tarDir);

    void download(String bucketName, String fileName, HttpServletResponse res);

    void download(String bucketName, String dirpath, HttpServletResponse response, boolean recursive);

    boolean doesObjectExist(String bucketName, String objectName);
}
