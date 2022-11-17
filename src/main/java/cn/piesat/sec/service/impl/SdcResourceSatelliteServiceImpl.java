package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.common.mybatisplus.exception.DdResponseType;
import cn.piesat.kjyy.common.mybatisplus.utils.Query ;
import cn.piesat.kjyy.common.mybatisplus.utils.ConditionBuilder  ;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.kjyy.core.utils.CopyBean  ;
import cn.piesat.sec.dao.mapper.SdcResourceSatelliteMapper;
import cn.piesat.sec.exception.SecException;
import cn.piesat.sec.model.dto.SdcResourceSatelliteDTO;
import cn.piesat.sec.model.entity.SdcResourceSatelliteDO;
import cn.piesat.sec.model.query.SdcResourceSatelliteQuery;
import cn.piesat.sec.model.vo.SdcResourceSatelliteVO;
import cn.piesat.sec.service.SdcResourceSatelliteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import java.io.Serializable;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.ObjectUtils;

/**
 * 卫星基本信息Service实现类
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2022-11-14 16:35:45
 */
@Service("sdcResourceSatelliteService")
public class SdcResourceSatelliteServiceImpl extends ServiceImpl<SdcResourceSatelliteMapper, SdcResourceSatelliteDO> implements SdcResourceSatelliteService {

    @Override
    public PageResult list(PageBean pageBean, SdcResourceSatelliteQuery sdcResourceSatelliteQuery) {
        QueryWrapper<SdcResourceSatelliteDO> wrapper = null;
        if (ObjectUtils.isEmpty(sdcResourceSatelliteQuery)) {
            wrapper = new QueryWrapper<>();
        } else {
            wrapper = ConditionBuilder.build(sdcResourceSatelliteQuery);
        }
        IPage<SdcResourceSatelliteDO> page = this.page(Query.getPage(pageBean), wrapper);
        return new PageResult(page.getTotal(), CopyBean.copy(page.getRecords(), SdcResourceSatelliteVO::new));
    }

    @Override
    public SdcResourceSatelliteVO info(Serializable id) {
        SdcResourceSatelliteDO sdcResourceSatelliteDO = getById(id);
        return CopyBean.copy(sdcResourceSatelliteDO,SdcResourceSatelliteVO::new);
    }
    private void checkSatIdUnique(String satId,Serializable id) {
        LambdaQueryWrapper<SdcResourceSatelliteDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SdcResourceSatelliteDO::getSatId,satId);
        if(!ObjectUtils.isEmpty(id)){
            wrapper.ne(SdcResourceSatelliteDO::getId,id);
        }
        long count = this.count(wrapper);
        if (count>0){
            throw  new SecException(DdResponseType.RECORD_REPEAT);
        }
    }
    @Override
    public Boolean save(SdcResourceSatelliteDTO sdcResourceSatelliteDTO) {
        checkSatIdUnique(sdcResourceSatelliteDTO.getSatId(),null);
        SdcResourceSatelliteDO sdcResourceSatelliteDO = CopyBean.copy(sdcResourceSatelliteDTO, SdcResourceSatelliteDO::new);
        return save(sdcResourceSatelliteDO);
    }

    @Override
    public Boolean update(SdcResourceSatelliteDTO sdcResourceSatelliteDTO) {
        checkSatIdUnique(sdcResourceSatelliteDTO.getSatId(),sdcResourceSatelliteDTO.getId());
        SdcResourceSatelliteDO sdcResourceSatelliteDO = this.getById(sdcResourceSatelliteDTO.getId());
        BeanUtils.copyProperties(sdcResourceSatelliteDTO,sdcResourceSatelliteDO);
        return updateById(sdcResourceSatelliteDO);
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
