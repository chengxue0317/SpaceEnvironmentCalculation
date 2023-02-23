package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.common.mybatisplus.utils.Query ;
import cn.piesat.sec.dao.mapper.FieldManageMapper;
import cn.piesat.sec.model.entity.FieldManageDO;
import cn.piesat.sec.service.FieldManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service("fieldManageService")
public class FieldManageServiceImpl extends ServiceImpl<FieldManageMapper, FieldManageDO> implements FieldManageService {


    @Autowired
    private FieldManageMapper fieldManageMapper;

    @Override
    public PageResult list(PageBean pageBean, FieldManageDO fieldManageDO) {
        QueryWrapper<FieldManageDO> wrapper = new QueryWrapper<>(fieldManageDO);
        IPage<FieldManageDO> page = this.page(
                Query.getPage(pageBean),
                wrapper
        );
        return new PageResult(page.getTotal(), page.getRecords());
    }

    @Override
    public FieldManageDO info(Long id) {
        return getById(id);
    }
    @Override
    public Boolean add(FieldManageDO fieldManageDO) {
        return save(fieldManageDO);
    }

    @Override
    public Boolean update(FieldManageDO fieldManageDO) {
        return updateById(fieldManageDO) ;
    }

    @Override
    public Boolean delete(List<Long> asList) {
        return removeBatchByIds(asList);
    }

    @Override
    public Boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public Integer existsTable(String tableName) {
        return fieldManageMapper.existsTable(tableName);
    }
}
