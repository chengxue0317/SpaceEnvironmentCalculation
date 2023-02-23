package cn.piesat.sec.dao.mapper;

import cn.piesat.sec.model.entity.FieldManageDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 字段管理表
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2023-02-20 16:36:46
 */
@Mapper
public interface FieldManageMapper extends BaseMapper<FieldManageDO> {

    /**
     * 判断表是否存在
     *
     * @param tableName 表名称
     * @return 结果
     * @author sw
     */
    @Select(" select COUNT(*) from  sysobjects where NAME = #{tableName}")
    Integer existsTable(@Param("tableName") String tableName);
}
