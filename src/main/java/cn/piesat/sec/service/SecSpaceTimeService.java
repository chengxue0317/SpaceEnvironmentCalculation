package cn.piesat.sec.service;

public interface SecSpaceTimeService {
    /**
     * 获取指定类型、时段的数据文件信息
     *
     * @param fileType  文件类型
     * @param filePath  文件路径
     * @param localDate 数据日期
     * @return 文件信息对象
     */
    String uploadData(String fileType, String filePath, String localDate);

    /**
     * 通知时空数据来了
     *
     * @return 数据对象信息
     */
    Boolean giveNotice(String topic, String message);
}
