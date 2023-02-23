package cn.piesat.sec.utils;

import cn.piesat.sec.model.vo.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @创建人 jiangchun
 * @创建时间 2019/10/23
 * @描述
 */
@Slf4j
@Component
public class PageUtil {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public PageVo queryForPage(String sql, Integer pageNum, Integer pageSize, Object[] params) throws DataAccessException {
        if (pageNum == null || pageSize == null) {
            return null;
        }
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        PageVo result = new PageVo();

        //获取记录条数
        String countSql = "select count(1) as count from (" + sql + ") temp";
        log.info("countSql {}", countSql);

        List countList = jdbcTemplate.queryForList(countSql, params, Integer.class);

        result.setTotal((Integer) countList.get(0));
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);

        int pageCount = result.getTotal() % result.getPageSize();
        result.setPages(pageCount == 0 ? (result.getTotal() / result.getPageSize()) : (result.getTotal() / result.getPageSize() + 1));

        sql += parseLimit(result);
        log.info("queryLimitSql {}", sql);

//        List data = jdbcTemplate.query(sql, params, rowMapper);
        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, params);
        result.setList(data);
        return result;
    }

    private String parseLimit(PageVo pagination) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" limit ");
        stringBuffer.append(pagination.getPageSize());

        stringBuffer.append(" offset ");
        stringBuffer.append(pagination.getPageSize()*(pagination.getPageNum()-1));

        return stringBuffer.toString();
    }


}

