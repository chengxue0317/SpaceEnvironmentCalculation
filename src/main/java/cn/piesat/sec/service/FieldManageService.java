package cn.piesat.sec.service;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.piesat.sec.model.entity.FieldManageDO;

import java.util.List;

/**
 * 字段管理表Service接口
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2023-02-20 16:36:46
 */
public interface FieldManageService extends IService<FieldManageDO> {

    PageResult list(PageBean pageBean, FieldManageDO fieldManageDO);

    FieldManageDO info(Long id);

    Boolean add(FieldManageDO fieldManageDO);

    Boolean update(FieldManageDO fieldManageDO);

    Boolean delete(List<Long> asList);

    Boolean delete(Long id);

    Integer existsTable(String tableName);

}

