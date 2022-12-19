package cn.piesat.sec.service;

import cn.piesat.sec.model.vo.SecSpaceTimeVO;

import java.util.List;

public interface SecSpaceTimeService {
    /**
     * 获取指定类型、时段的数据文件信息
     *
     * @param fileType  文件类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 文件信息对象
     */
    SecSpaceTimeVO getSpaceTimeFileInfo(String fileType, String startTime, String endTime);

    /**
     * 获取指定类型、时段的数据文件信息
     *
     * @param fileType  文件类型
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 文件信息对象
     */
    SecSpaceTimeVO getFileInformation(String fileType, String startTime, String endTime);

    /**
     * 获取buket下的所有文件信息
     *
     * @return 文件信息列表
     */
    List<Object> list();

    /**
     * 通知时空数据来了
     *
     * @return 数据对象信息
     */
    SecSpaceTimeVO giveNotice();

    /**
     * 文件上传
     *
     * @param filesPath 文件路径
     * @return 文件上传结果
     */
    boolean uploadFiles(List<String> filesPath);
}
