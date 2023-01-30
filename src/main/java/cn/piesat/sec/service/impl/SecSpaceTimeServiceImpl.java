package cn.piesat.sec.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.constant.SpaceTimeConst;
import cn.piesat.sec.comm.properties.SecFileServerProperties;
import cn.piesat.sec.comm.util.MinioUtil;
import cn.piesat.sec.dao.mapper.SecSpaceTimeServiceMapper;
import cn.piesat.sec.model.vo.SecSpaceTimeVO;
import cn.piesat.sec.service.SecSpaceTimeService;
import com.alibaba.fastjson.JSON;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class SecSpaceTimeServiceImpl implements SecSpaceTimeService {
    private static Logger logger = LoggerFactory.getLogger(SecSpaceTimeServiceImpl.class);

    @Autowired
    private MinioClient minioClient;

    @Resource
    private SecFileServerProperties secFileServerProperties;

    @Value("${minio.bucketName}")
    private String buketName;

    @Resource
    private SecSpaceTimeServiceMapper secSpaceTimeServiceMapper;

    @Resource
    private MinioUtil minioUtil;

    @Override
    public SecSpaceTimeVO getSpaceTimeFileInfo(String fileType, String startTime, String endTime) {
        SecSpaceTimeVO vo = new SecSpaceTimeVO();
        List<String> heads = SpaceTimeConst.FILE_TYPE_HEAD.get(fileType);
        String table = SpaceTimeConst.FILE_TYPE_TABLE.get(fileType);

        StringBuilder sqlbuder = new StringBuilder("SELECT DISTINCT ").append(StringUtils.join(heads).replace("[", "").replace("]", ""))
                .append(" FROM ").append(table).append(" WHERE TIME >= '").append(startTime).append("'")
                .append(" and TIME <= '").append(endTime).append("'");
        StringBuilder sqlCount = new StringBuilder("SELECT COUNT(*) FROM (").append(sqlbuder).append(")");
        try {
            int pageSize = 50;
            String condition = "";
            Integer count = secSpaceTimeServiceMapper.getSpaceTimeDataCount(sqlCount.toString());
            String fileName = SpaceTimeConst.FILE_TYPE_NAME.get(fileType).concat(".txt");
            String path = secFileServerProperties.getProfile().concat("spacetime").concat(Constant.FILE_SEPARATOR).concat(fileName);
            List<String> pathList = new ArrayList<>();
            pathList.add(path);
            if (FileUtils.getFile(path).exists()) {
                FileUtils.forceDeleteOnExit(FileUtils.getFile(path));
            } else {
                FileUtils.forceMkdir(FileUtils.getFile(path).getParentFile());
            }
            vo.setFileType(fileType);
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            vo.setPath(pathList);
            if (count > pageSize) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(FileUtils.getFile(path), true), Constant.BUFFSIZE);
                List<String> tableHead = SpaceTimeConst.FILE_TYPE_HEAD.get(fileType);
                writer.write(StringUtils.join(tableHead, " "));
                writer.newLine();
                writer.flush();
                int circle = (int) Math.ceil(count * 1.0 / pageSize);
                for (int i = 0; i < circle; i++) {
                    int endLimit = (i + 1) * pageSize;
                    endLimit = endLimit <= count ? endLimit : count;
                    condition = " ORDER BY TIME ASC OFFSET " + i * pageSize + " LIMIT " + endLimit;
                    List<Map<String, String>> spaceTimeFileInfo = secSpaceTimeServiceMapper.getSpaceTimeFileInfo(sqlbuder.toString().concat(condition));
                    spaceTimeFileInfo.forEach(item -> {
                        try {
                            writer.write(CollectionUtil.join(item.values(), " "));
                            writer.newLine();
                            writer.flush();
                        } catch (IOException e) {
                            logger.error(String.format(Locale.ROOT, "", e.getMessage()));
                        }
                    });
                }
                writer.flush();
                writer.close();
            } else {
                List<Map<String, String>> spaceTimeFileInfo = secSpaceTimeServiceMapper.getSpaceTimeFileInfo(sqlbuder.toString());
                BufferedWriter writer = new BufferedWriter(new FileWriter(FileUtils.getFile(path), true), Constant.BUFFSIZE);
                spaceTimeFileInfo.forEach(item -> {
                    try {
                        writer.write(CollectionUtil.join(item.values(), " "));
                        writer.newLine();
                        writer.flush();
                    } catch (IOException e) {
                        logger.error(String.format(Locale.ROOT, "", e.getMessage()));
                    }
                });
                writer.close();
            }
        } catch (Exception e) {
            logger.error(String.format(Locale.ROOT, "----Select data to space time throw exception %s", e.getMessage()));
        }
        return vo;
    }

    @Override
    public SecSpaceTimeVO getFileInformation(String fileType, String startTime, String endTime) {
        List<Item> myObjects = minioUtil.listObjects(buketName);
        List<String> pathList = new ArrayList<>();
        SecSpaceTimeVO vo = new SecSpaceTimeVO();
        vo.setFileType(fileType);
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        String start = startTime.replaceAll("[- :]", "");
        String end = endTime.replaceAll("[- :]", "");
        if (CollectionUtils.isNotEmpty(myObjects)) {
            for (Item item : myObjects) {
                String fileName = SpaceTimeConst.FILE_TYPE_NAME.get(fileType);
                String objName = item.objectName();
                if (objName.toLowerCase(Locale.ROOT).indexOf(fileName.toLowerCase(Locale.ROOT)) > -1) {
                    String left = fileName.concat(start).concat(".txt");
                    String right = fileName.concat(end).concat(".txt");
                    String onlyName = objName.substring(objName.lastIndexOf(Constant.FILE_SEPARATOR) + 1);
                    if (onlyName.compareTo(left) >= 0 && onlyName.compareTo(right) <= 0) {
                        pathList.add(buketName.concat(Constant.FILE_SEPARATOR).concat(objName));
                    }
                }
            }
        }

        vo.setPath(pathList);
        if (pathList.size() == 0) {
            vo.setMessage("没有找到符合条件的文件信息");
        }
        return vo;
    }

    @Override
    public List<Object> list() {
        Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(buketName).recursive(true).build());
        // 获取一个buket下的所有文件
        Iterator<Result<Item>> iterator = myObjects.iterator();
        List<Object> list = new ArrayList<>();
        String format = "{'fileName':'%s','fileSize':'%s'}";
        while (iterator.hasNext()) {
            try {
                Item item = iterator.next().get();
                list.add(JSON.parse(String.format(format, item.objectName(), item.size())));
            } catch (ErrorResponseException e) {
                logger.error(String.format(Locale.ROOT, "---ErrorResponseException %s", e.getMessage()));
            } catch (InsufficientDataException e) {
                logger.error(String.format(Locale.ROOT, "---InsufficientDataException %s", e.getMessage()));
            } catch (InternalException e) {
                logger.error(String.format(Locale.ROOT, "---InternalException %s", e.getMessage()));
            } catch (InvalidKeyException e) {
                logger.error(String.format(Locale.ROOT, "---InvalidKeyException %s", e.getMessage()));
            } catch (InvalidResponseException e) {
                logger.error(String.format(Locale.ROOT, "---InvalidResponseException %s", e.getMessage()));
            } catch (IOException e) {
                logger.error(String.format(Locale.ROOT, "---IOException %s", e.getMessage()));
            } catch (NoSuchAlgorithmException e) {
                logger.error(String.format(Locale.ROOT, "---NoSuchAlgorithmException %s", e.getMessage()));
            } catch (ServerException e) {
                logger.error(String.format(Locale.ROOT, "---ServerException %s", e.getMessage()));
            } catch (XmlParserException e) {
                logger.error(String.format(Locale.ROOT, "---XmlParserException %s", e.getMessage()));
            }
        }
        return list;
    }

    @Override
    public SecSpaceTimeVO giveNotice() {
        return null;
    }

    @Override
    public boolean uploadFiles(List<String> filesPath) {
        try {
            // 1.检查存储桶是否已经存在
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(buketName).build());
            if (found) {
                logger.info("Bucket already exists");
            } else {
                minioUtil.makeBucket(buketName);
            }
            // 多文件上传
            List<SnowballObject> objects = new ArrayList<SnowballObject>();
            for (String path : filesPath) {
                File file = FileUtils.getFile(path);
                objects.add(
                        new SnowballObject(
                                file.getName(),
                                FileUtils.openInputStream(file),
                                file.length(),
                                null));
            }
            minioClient.uploadSnowballObjects(
                    UploadSnowballObjectsArgs.builder().bucket(buketName).objects(objects).build());
        } catch (ErrorResponseException e) {
            logger.error(String.format(Locale.ROOT, "===ErrorResponseException %s", e.getMessage()));
        } catch (InsufficientDataException e) {
            logger.error(String.format(Locale.ROOT, "===InsufficientDataException %s", e.getMessage()));
        } catch (InternalException e) {
            logger.error(String.format(Locale.ROOT, "===InternalException %s", e.getMessage()));
        } catch (InvalidKeyException e) {
            logger.error(String.format(Locale.ROOT, "===InvalidKeyException %s", e.getMessage()));
        } catch (InvalidResponseException e) {
            logger.error(String.format(Locale.ROOT, "===InvalidResponseException %s", e.getMessage()));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "===IOException %s", e.getMessage()));
        } catch (NoSuchAlgorithmException e) {
            logger.error(String.format(Locale.ROOT, "===NoSuchAlgorithmException %s", e.getMessage()));
        } catch (ServerException e) {
            logger.error(String.format(Locale.ROOT, "===ServerException %s", e.getMessage()));
        } catch (XmlParserException e) {
            logger.error(String.format(Locale.ROOT, "===XmlParserException %s", e.getMessage()));
        }
        return true;
    }
}
