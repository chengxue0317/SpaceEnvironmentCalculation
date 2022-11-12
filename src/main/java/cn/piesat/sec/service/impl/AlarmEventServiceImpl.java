package cn.piesat.sec.service.impl;

import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.dao.mapper.AlarmEventMapper;
import cn.piesat.sec.model.query.AlarmEventQuery;
import cn.piesat.sec.model.vo.AlarmEventVO;
import cn.piesat.sec.service.AlarmEventService;
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
        List<AlarmEventVO> list;
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
}
