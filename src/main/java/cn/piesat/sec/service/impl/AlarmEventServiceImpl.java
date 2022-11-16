package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.dao.mapper.AlarmEventMapper;
import cn.piesat.sec.model.entity.AlarmEventDO;
import cn.piesat.sec.model.entity.SecAlarmForecastDO;
import cn.piesat.sec.model.query.AlarmEventQuery;
import cn.piesat.sec.model.vo.AlarmEventForecastVO;
import cn.piesat.sec.service.AlarmEventService;
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
public class AlarmEventServiceImpl implements AlarmEventService {
    @Autowired
    private AlarmEventMapper alarmEventMapper;

    @Override
    public PageResult list(PageBean pageBean, AlarmEventQuery alarmEventQuery) {
        int pageSize = pageBean.getSize();
        long offset = (pageBean.getPage() - 1) * pageSize;
        LocalDateTime startTime = alarmEventQuery.getStartTime();
        LocalDateTime endTime = alarmEventQuery.getEndTime();
        String[] levelArr = alarmEventQuery.getLevel();
        Set<String> levelList = new HashSet<>();
        if (levelArr != null && levelArr.length > 0) {
            for (String s : levelArr) {
                if (s.equalsIgnoreCase("2") || s.equalsIgnoreCase("3")) {
                    levelList.add("2"); // 数据库实际2、3都表示橙色警报
                    levelList.add("3");
                } else if (s.equalsIgnoreCase("4") || s.equalsIgnoreCase("5")) {
                    levelList.add("4");// 数据库实际4、5都表示橙色警报
                    levelList.add("5");
                } else {
                    levelList.add(s);
                }
            }
        }
        String level = levelArr == null || levelArr.length == 0 ? ">0" : "in (".concat(StringUtils.join(levelList, ",")).concat(")");
        String type = alarmEventQuery.getType();
        Long total;
        List<AlarmEventDO> list;
        switch (type.toLowerCase(Locale.ROOT)) {
            case "xray": {
                total = alarmEventMapper.getAlarmEventDataCount("SEC_XRAY_ALARM", startTime, endTime, level);
                list = alarmEventMapper.getAlarmEventDataList("'xray'", "SEC_XRAY_ALARM", startTime, endTime, level, offset, pageSize);
                break;
            }
            case "proton": {
                total = alarmEventMapper.getAlarmEventDataCount("SEC_PROTON_ALARM", startTime, endTime, level);
                list = alarmEventMapper.getAlarmEventDataList("'proton'", "SEC_PROTON_ALARM", startTime, endTime, level, offset, pageSize);
                break;
            }
            case "electron": {
                total = alarmEventMapper.getAlarmEventDataCount("SEC_ELE_ALARM", startTime, endTime, level);
                list = alarmEventMapper.getAlarmEventDataList("'electron'", "SEC_ELE_ALARM", startTime, endTime, level, offset, pageSize);
                break;
            }
            case "geomagnetic": {
                total = alarmEventMapper.getAlarmEventDataCount("SEC_DST_ALARM", startTime, endTime, level);
                list =
                    alarmEventMapper.getAlarmEventDataList("'geomagnetic'", "SEC_DST_ALARM", startTime, endTime, level, offset, pageSize);
                break;
            }
            default: {
                total = alarmEventMapper.getAlarmEventsDataCount(startTime, endTime, level);
                list = alarmEventMapper.getAlarmEventsDataList(startTime, endTime, level, offset, pageSize);
            }
        }
        return new PageResult(total, list);
    }

    @Override
    public PageResult getAlarmEvent3daysForecast() {
        // 当天警报数据
        List<AlarmEventDO> xrayList = alarmEventMapper.getTodayAlarmEvent("SEC_XRAY_ALARM");
        checkRowData(xrayList);
        List<AlarmEventDO> protonList = alarmEventMapper.getTodayAlarmEvent("SEC_PROTON_ALARM");
        checkRowData(protonList);
        List<AlarmEventDO> eleList = alarmEventMapper.getTodayAlarmEvent("SEC_ELE_ALARM");
        checkRowData(eleList);
        List<AlarmEventDO> dstList = alarmEventMapper.getTodayAlarmEvent("SEC_DST_ALARM");
        checkRowData(dstList);

        // 未来三天警报预报数据
        List<SecAlarmForecastDO> alarmEvent3daysForecast = alarmEventMapper.getAlarmEvent3daysForecast();
        AlarmEventForecastVO aevo = new AlarmEventForecastVO();
        aevo.setXray(xrayList.get(0));
        aevo.setTime("当前");
        aevo.setProton(protonList.get(0));
        aevo.setElectron(eleList.get(0));
        aevo.setGeomagnetic(dstList.get(0));
        List<AlarmEventForecastVO> list = new ArrayList<>();
        list.add(aevo);
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

    private void checkRowData(List<AlarmEventDO> rowList) {
        if (CollectionUtils.isEmpty(rowList)) {
            rowList.add(new AlarmEventDO());
        }
    }

    private void combinNodataRowsData(List<AlarmEventForecastVO> list) {
        AlarmEventForecastVO vo24 = new AlarmEventForecastVO();
        vo24.setTime("24h");
        AlarmEventForecastVO vo48 = new AlarmEventForecastVO();
        vo48.setTime("48h");
        AlarmEventForecastVO vo72 = new AlarmEventForecastVO();
        vo72.setTime("72h");
        list.add(vo24);
        list.add(vo48);
        list.add(vo72);
    }

    private void combinRowData(List<AlarmEventForecastVO> list, String time, String xrayStr, String protonStr, String eleStr,
        String dstStr) {
        AlarmEventForecastVO vo = new AlarmEventForecastVO();
        vo.setTime(time);
        vo.setXray(combinCellData(xrayStr));
        vo.setProton(combinCellData(protonStr));
        vo.setElectron(combinCellData(eleStr));
        vo.setGeomagnetic(combinCellData(dstStr));
        list.add(vo);
    }

    private AlarmEventDO combinCellData(String alarmStr) {
        AlarmEventDO vo = new AlarmEventDO();
        if (StringUtils.isEmpty(alarmStr)) {
            vo.setLevel(0);
        } else {
            Integer level = vo.getLevel();
            level = level == null ? 0 : level;
            if (alarmStr.indexOf("红") != -1 && level < 5) {
                vo.setLevel(5);
                level = vo.getLevel();
            }
            if (alarmStr.indexOf("橙") != -1 && level < 3) {
                vo.setLevel(3);
                level = vo.getLevel();
            }
            if (alarmStr.indexOf("黄") != -1 && level < 1) {
                vo.setLevel(1);
            }
        }
        vo.setContent(alarmStr);
        return vo;
    }
}
