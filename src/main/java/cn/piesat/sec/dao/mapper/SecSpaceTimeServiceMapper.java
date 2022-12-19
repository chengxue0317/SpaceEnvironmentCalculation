package cn.piesat.sec.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SecSpaceTimeServiceMapper {
    /**
     * 获取数据总条数
     *
     * @param sql sql数据库查询语句
     * @return
     * @throws Exception
     */
    Integer getSpaceTimeDataCount(@Param("sql") String sql) throws Exception;

    /**
     * 获取指定类型、时段的数据文件信息
     *
     * @param sql sql语句
     * @return 文件信息对象
     */
    List<Map<String, String>> getSpaceTimeFileInfo(@Param("sql") String sql) throws Exception;
}
