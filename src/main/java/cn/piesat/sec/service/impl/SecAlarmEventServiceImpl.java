package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.dao.mapper.SecAlarmEventMapper;
import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import cn.piesat.sec.model.entity.SecProtonAlarmDO;
import cn.piesat.sec.model.query.SecAlarmEventQuery;
import cn.piesat.sec.model.vo.SecAlarmEventForecastVO;
import cn.piesat.sec.service.SecAlarmEventService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * desc
 *
 * @author wuyazhou
 * @date 2022-11-10
 */
@Service("alarmEventService")
public class SecAlarmEventServiceImpl implements SecAlarmEventService {
    @Autowired
    private SecAlarmEventMapper secAlarmEventMapper;

    @Override
    public PageResult list(PageBean pageBean, SecAlarmEventQuery secAlarmEventQuery) {
        int pageSize = pageBean.getSize();
        long offset = (pageBean.getPage() - 1) * pageSize;
        LocalDateTime startTime = secAlarmEventQuery.getStartTime();
        LocalDateTime endTime = secAlarmEventQuery.getEndTime();
        String[] levelArr = secAlarmEventQuery.getLevel();
        List<String> levelList = Arrays.asList(levelArr);
        String level = levelArr == null || levelArr.length == 0 ? ">0" : "in (".concat(StringUtils.join(levelList, ",")).concat(")");
        String type = secAlarmEventQuery.getType();
        Long total = null;
        List<SecProtonAlarmDO> list = null;
        try {
            switch (type.toLowerCase(Locale.ROOT)) {
                case "xray": {
                    total = secAlarmEventMapper.getAlarmEventDataCount("SEC_XRAY_ALARM", startTime, endTime, level);
                    list = secAlarmEventMapper.getAlarmEventDataList("'xray'", "SEC_XRAY_ALARM", startTime, endTime, level, offset, pageSize);
                    break;
                }
                case "proton": {
                    total = secAlarmEventMapper.getAlarmEventDataCount("SEC_PROTON_ALARM", startTime, endTime, level);
                    list = secAlarmEventMapper.getAlarmEventDataList("'proton'", "SEC_PROTON_ALARM", startTime, endTime, level, offset, pageSize);
                    break;
                }
                case "electron": {
                    total = secAlarmEventMapper.getAlarmEventDataCount("SEC_ELE_ALARM", startTime, endTime, level);
                    list = secAlarmEventMapper.getAlarmEventDataList("'electron'", "SEC_ELE_ALARM", startTime, endTime, level, offset, pageSize);
                    break;
                }
                case "geomagnetic": {
                    total = secAlarmEventMapper.getAlarmEventDataCount("SEC_DST_ALARM", startTime, endTime, level);
                    list =
                            secAlarmEventMapper.getAlarmEventDataList("'geomagnetic'", "SEC_DST_ALARM", startTime, endTime, level, offset, pageSize);
                    break;
                }
                default: {
                    total = secAlarmEventMapper.getAlarmEventsDataCount(startTime, endTime, level);
                    list = secAlarmEventMapper.getAlarmEventsDataList(startTime, endTime, level, offset, pageSize);
                }
            }
        } catch (Exception e) {
            total = 0L;
            list = new ArrayList<>();
        } finally {
            return new PageResult(total, list);
        }
    }

    @Override
    public PageResult getAlarmEvent3daysForecast() {
        // 当天警报数据
        List<SecProtonAlarmDO> xrayList = secAlarmEventMapper.getTodayAlarmEvent("SEC_XRAY_ALARM");
        checkRowData(xrayList);
        List<SecProtonAlarmDO> protonList = secAlarmEventMapper.getTodayAlarmEvent("SEC_PROTON_ALARM");
        checkRowData(protonList);
        List<SecProtonAlarmDO> eleList = secAlarmEventMapper.getTodayAlarmEvent("SEC_ELE_ALARM");
        checkRowData(eleList);
        List<SecProtonAlarmDO> dstList = secAlarmEventMapper.getTodayAlarmEvent("SEC_DST_ALARM");
        checkRowData(dstList);

        SecAlarmEventForecastVO aevo = new SecAlarmEventForecastVO();
        aevo.setXray(xrayList.get(0));
        aevo.setTime("当前");
        aevo.setProton(protonList.get(0));
        aevo.setElectron(eleList.get(0));
        aevo.setGeomagnetic(dstList.get(0));
        List<SecAlarmEventForecastVO> list = new ArrayList<>();
        list.add(aevo);

        // 未来三天警报预报数据
        List<SecAlarmForecastDO> alarmEvent3daysForecast = secAlarmEventMapper.getAlarmEvent3daysForecast();
        if (CollectionUtils.isEmpty(alarmEvent3daysForecast)) {
            combinNodataRowsData(list);
        } else {
            SecAlarmForecastDO secAlarmForecastDO = alarmEvent3daysForecast.get(0);
            // 未来24小时
            String sxr1 = secAlarmForecastDO.getSxr1();
            String spe1 = secAlarmForecastDO.getSpe1();
            String ree1 = secAlarmForecastDO.getRee1();
            String gsma1 = secAlarmForecastDO.getGsma1();
            combinRowData(list, "24h", sxr1, spe1, ree1, gsma1);

            // 未来48小时
            String sxr2 = secAlarmForecastDO.getSxr2();
            String spe2 = secAlarmForecastDO.getSpe2();
            String ree2 = secAlarmForecastDO.getRee2();
            String gsma2 = secAlarmForecastDO.getGsma2();
            combinRowData(list, "48h", sxr2, spe2, ree2, gsma2);

            // 未来72小时
            String sxr3 = secAlarmForecastDO.getSxr3();
            String spe3 = secAlarmForecastDO.getSpe3();
            String ree3 = secAlarmForecastDO.getRee3();
            String gsma3 = secAlarmForecastDO.getGsma3();
            combinRowData(list, "72h", sxr3, spe3, ree3, gsma3);
        }
        return new PageResult(4L, list);
    }

    private void checkRowData(List<SecProtonAlarmDO> rowList) {
        if (CollectionUtils.isEmpty(rowList)) {
            rowList.add(new SecProtonAlarmDO());
        }
    }

    private void combinNodataRowsData(List<SecAlarmEventForecastVO> list) {
        SecAlarmEventForecastVO vo24 = new SecAlarmEventForecastVO();
        vo24.setTime("24h");
        SecAlarmEventForecastVO vo48 = new SecAlarmEventForecastVO();
        vo48.setTime("48h");
        SecAlarmEventForecastVO vo72 = new SecAlarmEventForecastVO();
        vo72.setTime("72h");
        list.add(vo24);
        list.add(vo48);
        list.add(vo72);
    }

    private void combinRowData(List<SecAlarmEventForecastVO> list, String time, String xrayStr, String protonStr, String eleStr,
                               String dstStr) {
        SecAlarmEventForecastVO vo = new SecAlarmEventForecastVO();
        vo.setTime(time);
        vo.setXray(combinCellData(xrayStr));
        vo.setProton(combinCellData(protonStr));
        vo.setElectron(combinCellData(eleStr));
        vo.setGeomagnetic(combinCellData(dstStr));
        list.add(vo);
    }

    private SecProtonAlarmDO combinCellData(String alarmStr) {
        SecProtonAlarmDO vo = new SecProtonAlarmDO();
        if (StringUtils.isEmpty(alarmStr)) {
            vo.setLevel(0);
        } else {
            Integer level = vo.getLevel();
            level = level == null ? 0 : level;
            if (alarmStr.indexOf("红") != -1 && level < 3) {
                level = 3;
                vo.setLevel(level);
                vo.setOverview(findAnyMatchStr(alarmStr.split(Constant.SEMICOLON), "红"));
            }
            if (alarmStr.indexOf("橙") != -1 && level < 2) {
                level = 2;
                vo.setLevel(level);
                vo.setOverview(findAnyMatchStr(alarmStr.split(Constant.SEMICOLON), "橙"));
            }
            if (alarmStr.indexOf("黄") != -1 && level < 1) {
                level = 1;
                vo.setLevel(level);
                vo.setOverview(findAnyMatchStr(alarmStr.split(Constant.SEMICOLON), "黄"));
            }
        }
        vo.setContent(alarmStr);
        return vo;
    }

    private String findAnyMatchStr(String[] arr, String str) {
        if (arr == null || arr.length == 0 || str == null || str.length() == 0) {
            return StringUtils.EMPTY;
        } else {
            for (String item : arr) {
                if (item.indexOf(str) > -1) {
                    return item;
                }
            }
        }
        return StringUtils.EMPTY;
    }
}
