package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.exception.DdResponseType;
import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder;
import cn.piesat.kjyy.common.mybatisplus.utils.Query;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean;
import cn.piesat.sec.dao.mapper.SdcResourceStationMapper;
import cn.piesat.sec.exception.SecException;
import cn.piesat.sec.model.dto.SdcResourceStationDTO;
import cn.piesat.sec.model.entity.SdcResourceStationDO;
import cn.piesat.sec.model.query.SdcResourceStationQuery;
import cn.piesat.sec.model.vo.SdcResourceStationVO;
import cn.piesat.sec.service.SdcResourceStationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 测站基本信息Service实现类
 *
 * @author wuyazhou
 * @email wuyazhou@piesat.cn
 * @date 2022-12-13 14:18:47
 */
@Service("sdcResourceStationService")
public class SdcResourceStationServiceImpl extends ServiceImpl<SdcResourceStationMapper, SdcResourceStationDO> implements SdcResourceStationService {
    private static final Logger logger = LoggerFactory.getLogger(SdcResourceStationServiceImpl.class);

    @Autowired
    private SdcResourceStationMapper sdcResourceStationMapper;

    @Override
    public PageResult list(PageBean pageBean, SdcResourceStationQuery sdcResourceStationQuery) {
        QueryWrapper<SdcResourceStationDO> wrapper = null;
        if (ObjectUtils.isEmpty(sdcResourceStationQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(sdcResourceStationQuery);
        }
        IPage<SdcResourceStationDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SdcResourceStationVO::new));
    }

    @Override
    public List<SdcResourceStationVO> getAllList() {
        List<SdcResourceStationVO> resList = null;
        try {
            resList = sdcResourceStationMapper.getAllList();
        } catch (Exception e) {
            resList = new ArrayList<>();
            logger.error(String.format(Locale.ROOT, "=====The station data information failed to be obtained. %s", e.getMessage()));
        } finally {
            return resList;
        }
    }

    @Override
    public SdcResourceStationVO info(Serializable id) {
        SdcResourceStationDO sdcResourceStationDO = getById(id);
        return CopyBean.copy(sdcResourceStationDO, SdcResourceStationVO::new);
    }

    private void checkStaIdUnique(String staId, Serializable id) {
        LambdaQueryWrapper<SdcResourceStationDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SdcResourceStationDO::getStaId, staId);
        if (!ObjectUtils.isEmpty(id)) {
            wrapper.ne(SdcResourceStationDO::getId, id);
        }
        long count = this.count(wrapper);
        if (count > 0) {
            throw new SecException(DdResponseType.RECORD_REPEAT);
        }
    }

    @Override
    public Boolean save(SdcResourceStationDTO sdcResourceStationDTO) {
        checkStaIdUnique(sdcResourceStationDTO.getStaId(), null);
        SdcResourceStationDO sdcResourceStationDO = CopyBean.copy(sdcResourceStationDTO, SdcResourceStationDO::new);
        return save(sdcResourceStationDO);
    }

    @Override
    public Boolean update(SdcResourceStationDTO sdcResourceStationDTO) {
        checkStaIdUnique(sdcResourceStationDTO.getStaId(), sdcResourceStationDTO.getId());
        SdcResourceStationDO sdcResourceStationDO = this.getById(sdcResourceStationDTO.getId());
        BeanUtils.copyProperties(sdcResourceStationDTO, sdcResourceStationDO);
        return updateById(sdcResourceStationDO);
    }

    @Override
    public Boolean delete(List<Serializable> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    public Boolean delete(Serializable id) {
        return removeById(id);
    }
}
