package cn.piesat.sec.comm.oss;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IFileSystem {
    Boolean bucketExists(String bucketName);

    void makeBucket(String bucketName);

    String upload(String bucketName, MultipartFile file);

    String upload(String bucketName, String objectName, String fileName);

    String preview(String bucketName, String fileName);

    void download(String bucketName, String fileName, String tarDir);

    void download(String bucketName, String fileName, HttpServletResponse res);

    void download(String bucketName, String dirpath, HttpServletResponse response, boolean recursive);

    void download(String bucketName, List<String> filePath, HttpServletResponse response);

    boolean doesObjectExist(String bucketName, String objectName);
}
