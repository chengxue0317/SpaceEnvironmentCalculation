package cn.piesat.sec.controller;

import cn.piesat.sec.comm.oss.OSSInstance;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Api(tags = "S3文件存储测试")
@RestController
@RequestMapping("/spacetimes3")
@RequiredArgsConstructor
public class S3TestController {
    @Value("${s3.bucketName}")
    private String bucketName;

    @ApiOperation("创建存储桶")
    @PostMapping("makebucket")
    public Boolean makebucket() {
        OSSInstance.getOSSUtil().makeBucket(bucketName);
        return OSSInstance.getOSSUtil().bucketExists(bucketName);
    }

    @ApiOperation("上传文件")
    @PostMapping("uploada")
    public String uploada(@RequestPart("file") MultipartFile file) {
        return OSSInstance.getOSSUtil().upload(bucketName, file);
    }

    @ApiOperation("上传文件")
    @PostMapping("uploadb")
    public String upload(@RequestParam("dir") String dir, @RequestParam("path") String path) {
        return OSSInstance.getOSSUtil().upload(bucketName, dir.concat(path), path);
    }

    @ApiOperation("下载文件")
    @PostMapping("download")
    public void download(@RequestParam("path") String path, HttpServletResponse resp) {
        OSSInstance.getOSSUtil().download(bucketName, path, resp);
    }

    @ApiOperation("下载压缩文件")
    @PostMapping("download2")
    public void download2(@RequestParam("path") String path, HttpServletResponse resp) {
        OSSInstance.getOSSUtil().download(bucketName, path, resp, true);
    }

    @ApiOperation("文件预览")
    @PostMapping("lookatfile")
    public String upload2(@RequestParam("path") String path) {
        return OSSInstance.getOSSUtil().preview(bucketName, path);
    }

    @ApiOperation("文件是否存在")
    @PostMapping("doesObjectExist")
    public Boolean doesObjectExist(@RequestParam("path") String path) {
        return OSSInstance.getOSSUtil().doesObjectExist(bucketName, path);
    }
}
