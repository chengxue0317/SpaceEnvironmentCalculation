package cn.piesat.sec.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.AddGroup ;
import cn.piesat.kjyy.common.mybatisplus.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean ;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.entity.FieldManageDO;
import cn.piesat.sec.model.vo.PageVo;
import cn.piesat.sec.model.vo.SearchParam;
import cn.piesat.sec.service.FieldManageService;

import cn.piesat.sec.utils.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 字段管理表
 *
 * @author sw
 * @email shiwang@piesat.cn
 * @date 2023-02-20 16:36:46
 */
@Api(tags = "字段管理表")
@RestController
@RequestMapping("/fieldmanage")
@RequiredArgsConstructor
public class FieldManageController {

    private static final Logger log = LoggerFactory.getLogger(FieldManageController.class);

    @Autowired
    private final FieldManageService fieldManageService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 列表
     */
    @ApiOperation("分页查询")
    @PostMapping("/list")
    public PageResult list(PageBean pageBean, @RequestBody(required = false) FieldManageDO fieldManageDO){
        return fieldManageService.list(pageBean,fieldManageDO);

    }
    /**
     * 信息
     */
    @ApiOperation("根据id查询")
    @GetMapping("/info/{id}")
    public FieldManageDO info(@PathVariable("id") Long id){
        return fieldManageService.info(id);
    }
    /**
     * 保存
     */
    @ApiOperation("保存信息")
    @PostMapping("/save")
    public Boolean save(@Validated(AddGroup.class) @RequestBody FieldManageDO fieldManageDO){
        return fieldManageService.add(fieldManageDO);
    }

    /**
     * 修改
     */
    @ApiOperation("修改信息")
    @PutMapping("/update")
    public Boolean update(@Validated(UpdateGroup.class) @RequestBody FieldManageDO fieldManageDO){
        return fieldManageService.update(fieldManageDO);
    }

    /**
     * 删除
     */
    @ApiOperation("批量删除信息")
    @DeleteMapping("/delete")
    public Boolean delete(@RequestBody Long[] ids){
        return fieldManageService.delete(Arrays.asList(ids));
    }
    /**
     * 删除
     */
    @ApiOperation("根据id删除信息")
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Long id){
        return fieldManageService.delete(id);
    }

    /**
     * 创建或修改表
     */
    @ApiOperation("创建或修改表")
    @PostMapping("/changeTable")
    public Boolean changeTable(@RequestBody FieldManageDO fieldManageDO){
        //判断表是否存在，存在修改表，不存在创建表
        String tableName = "SEC_FAULT_DIAGNOSIS";
        Integer existsTable = fieldManageService.existsTable(tableName);
        if (existsTable==0){
            //创建表
            List<FieldManageDO> list = fieldManageService.list();
            StringBuilder sb = new StringBuilder();
            String primaryKey = null;
            for(FieldManageDO fieldManage:list){
                Integer notNullConstraint = fieldManage.getNotNullConstraint();
                if (notNullConstraint ==2){
                    primaryKey = fieldManage.getFieldName();
                    sb.append("\"");
                    sb.append(primaryKey);
                    sb.append("\" ");
                    sb.append(fieldManage.getDataType());
                    sb.append(" identity(1, 1) not null ,\n");
                }else {
                    sb.append("\"");
                    sb.append(fieldManage.getFieldName());
                    sb.append("\" ");
                    sb.append(fieldManage.getDataType());
                    sb.append(" (");
                    sb.append(fieldManage.getPrecision());
                    sb.append(") ");
                    if (notNullConstraint ==1){
                        sb.append(" not null ");
                    }
                    sb.append(",");
                    sb.append("\n");
                }
            }
            sb.append("primary key(\"");
            sb.append(primaryKey);
            sb.append("\")");
            sb.append("\n" +
                    ")\n" +
                    "storage(initial 1, next 1, minextents 1, fillfactor 0)\n" +
                    ";\n");
            String str = "create table \"SDC\".\""+tableName+"\"\n" +
                    "(\n";
            sb.insert(0,str);
            String sql = sb.toString();
            log.info("执行SQL语句----->{}",sql);
            jdbcTemplate.execute(sql);

            StringBuilder commentBuilder = new StringBuilder();
            commentBuilder.append("comment on table \"SDC\".\"");
            commentBuilder.append(tableName);
            commentBuilder.append("\" is '卫星空间环境故障诊断数据表';\n");
            String commentSql = commentBuilder.toString();
            log.info("执行SQL语句----->{}",commentSql);
            jdbcTemplate.execute(commentSql);

            for (FieldManageDO fieldManage2:list){
                StringBuilder fieldCommentBuilder = new StringBuilder();
                fieldCommentBuilder.append("comment on column \"SDC\".\"");
                fieldCommentBuilder.append(tableName);
                fieldCommentBuilder.append("\".\"");
                fieldCommentBuilder.append(fieldManage2.getFieldName());
                fieldCommentBuilder.append("\" is '");
                fieldCommentBuilder.append(fieldManage2.getAnnotation());
                fieldCommentBuilder.append("';\n");
                String fieldCommentSql = fieldCommentBuilder.toString();
                log.info("执行SQL语句----->{}",fieldCommentSql);
                jdbcTemplate.execute(fieldCommentSql);
            }


        }else if (existsTable==1){
            //修改表
            StringBuilder sqlBuilder = new StringBuilder();
            String fieldName = fieldManageDO.getFieldName();
            sqlBuilder.append("alter table \"SDC\".\"");
            sqlBuilder.append(tableName);
            sqlBuilder.append("\" add column(\"");
            sqlBuilder.append(fieldName);
            sqlBuilder.append("\" ");
            sqlBuilder.append(fieldManageDO.getDataType());
            sqlBuilder.append(" (");
            sqlBuilder.append(fieldManageDO.getPrecision());
            sqlBuilder.append(") ");
            if (fieldManageDO.getNotNullConstraint() ==1){
                sqlBuilder.append(" not null ");
            }
            sqlBuilder.append(");");
            String sql = sqlBuilder.toString();
            log.info("执行SQL语句----->{}", sql);
            jdbcTemplate.execute(sql);

            StringBuilder commentBuilder = new StringBuilder();
            commentBuilder.append("comment on column \"SDC\".\"");
            commentBuilder.append(tableName);
            commentBuilder.append("\".\"");
            commentBuilder.append(fieldName);
            commentBuilder.append("\" is '");
            commentBuilder.append(fieldManageDO.getAnnotation());
            commentBuilder.append("';");
            String commentSql = commentBuilder.toString();
            log.info("执行SQL语句----->{}",commentSql);
            jdbcTemplate.execute(commentSql);

        }

        return true;

    }


    /**
     * 新增字段
     */
    @ApiOperation("新增字段")
    @PostMapping("/addField")
    public void addField(@Validated(AddGroup.class) @RequestBody FieldManageDO fieldManageDO){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        StringBuilder sqlBuilder = new StringBuilder();
        String fieldName = fieldManageDO.getFieldName();
        sqlBuilder.append("alter table \"SDC\".\"");
        sqlBuilder.append(tableName);
        sqlBuilder.append("\" add column(\"");
        sqlBuilder.append(fieldName);
        sqlBuilder.append("\" ");
        sqlBuilder.append(fieldManageDO.getDataType());
        sqlBuilder.append(" (");
        sqlBuilder.append(fieldManageDO.getPrecision());
        sqlBuilder.append(") ");
        if (fieldManageDO.getNotNullConstraint() ==1){
            sqlBuilder.append(" not null ");
        }
        sqlBuilder.append(");");
        String sql = sqlBuilder.toString();
        log.info("执行SQL语句----->{}", sql);
        jdbcTemplate.execute(sql);

        StringBuilder commentBuilder = new StringBuilder();
        commentBuilder.append("comment on column \"SDC\".\"");
        commentBuilder.append(tableName);
        commentBuilder.append("\".\"");
        commentBuilder.append(fieldName);
        commentBuilder.append("\" is '");
        commentBuilder.append(fieldManageDO.getAnnotation());
        commentBuilder.append("';");
        String commentSql = commentBuilder.toString();
        log.info("执行SQL语句----->{}",commentSql);
        jdbcTemplate.execute(commentSql);
    }


    /**
     * 修改字段名称
     */
    @ApiOperation("修改字段名称")
    @PostMapping("/changeFieldName")
    public void changeFieldName(@RequestParam("oldFieldName") String oldFieldName,
                                @RequestParam("newFieldName") String newFieldName
                                ){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("alter table \"SDC\".\"");
        sqlBuilder.append(tableName);
        sqlBuilder.append("\" alter column \"");
        sqlBuilder.append(oldFieldName);
        sqlBuilder.append("\" ");
        sqlBuilder.append(" rename to ");
        sqlBuilder.append(newFieldName);
        sqlBuilder.append(";");
        String sql = sqlBuilder.toString();
        log.info("执行SQL语句----->{}", sql);
        jdbcTemplate.execute(sql);
    }


    /**
     * 修改字段类型
     */
    @ApiOperation("修改字段类型")
    @PostMapping("/changeDataType")
    public void changeDataType(@RequestParam("fieldName") String fieldName,
                                @RequestParam("dataType") String dataType
    ){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("alter table \"SDC\".\"");
        sqlBuilder.append(tableName);
        sqlBuilder.append("\" modify\"");
        sqlBuilder.append(fieldName);
        sqlBuilder.append("\" ");
        sqlBuilder.append(dataType);
        sqlBuilder.append(";");
        String sql = sqlBuilder.toString();
        log.info("执行SQL语句----->{}", sql);
        jdbcTemplate.execute(sql);
    }


    /**
     * 修改约束
     */
    @ApiOperation("修改约束")
    @PostMapping("/changeConstraint")
    public void changeConstraint(@RequestParam("oldFieldName") String oldFieldName,
                                 @RequestParam("notNullConstraint") Integer notNullConstraint
    ){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("alter table \"SDC\".\"");
        sqlBuilder.append(tableName);
        sqlBuilder.append("\" alter column \"");
        sqlBuilder.append(oldFieldName);
        sqlBuilder.append("\" ");
        if (notNullConstraint==0){
            sqlBuilder.append(" set null");
        }else if (notNullConstraint==1){
            sqlBuilder.append(" set not null");
        }
        sqlBuilder.append(";");
        String sql = sqlBuilder.toString();
        log.info("执行SQL语句----->{}", sql);
        jdbcTemplate.execute(sql);
    }


    /**
     * 修改精度
     */
    @ApiOperation("修改精度")
    @PostMapping("/changePrecision")
    public void changePrecision(@RequestParam("fieldName") String fieldName,
                               @RequestParam("dataType") String dataType,
                               @RequestParam("precision") Integer precision
    ){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("alter table \"SDC\".\"");
        sqlBuilder.append(tableName);
        sqlBuilder.append("\" modify\"");
        sqlBuilder.append(fieldName);
        sqlBuilder.append("\" ");
        sqlBuilder.append(dataType);
        sqlBuilder.append("(");
        sqlBuilder.append(precision);
        sqlBuilder.append(");");
        String sql = sqlBuilder.toString();
        log.info("执行SQL语句----->{}", sql);
        jdbcTemplate.execute(sql);
    }

    /**
     * 修改注释
     */
    @ApiOperation("修改注释")
    @PostMapping("/changeAnnotation")
    public void changeAnnotation(@RequestParam("fieldName") String fieldName,
                                @RequestParam("annotation") String annotation
    ){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("comment on column \"SDC\".\"");
        sqlBuilder.append(tableName);
        sqlBuilder.append("\".\"");
        sqlBuilder.append(fieldName);
        sqlBuilder.append("\" is '");
        sqlBuilder.append(annotation);
        sqlBuilder.append("';");
        String sql = sqlBuilder.toString();
        log.info("执行SQL语句----->{}", sql);
        jdbcTemplate.execute(sql);
    }

    /**
     * 删除字段
     */
    @ApiOperation("删除字段")
    @PostMapping("/delField")
    public void delField(@RequestParam("fieldName") String fieldName){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("alter table \"SDC\".\"");
        sqlBuilder.append(tableName);
        sqlBuilder.append("\" drop column \"");
        sqlBuilder.append(fieldName);
        sqlBuilder.append("\" ");
        sqlBuilder.append(";");
        String sql = sqlBuilder.toString();
        log.info("执行SQL语句----->{}", sql);
        jdbcTemplate.execute(sql);
    }

    /**
     * 插入数据
     */
    @ApiOperation("插入数据")
    @PostMapping("/insertData")
    public void insertData(@RequestBody List<Map<String,Object>> dataList){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        //拼接insert语句，只执行一次
        boolean firstFlag = true;
        //字段名
        StringBuilder sb = new StringBuilder(200);
        sb.append("insert into ").append(tableName).append(" (");
        //拼接字段值占位符
        StringBuilder sbValue = new StringBuilder(200);
        //存放数据的结果集
        List<Object[]> addResultList = new ArrayList<>();
        //单条数据结果
        Object[] rowResult;
        int size = 0;
        for(Map<String,Object> map:dataList){
            if(firstFlag){
                size = map.size();
            }
            rowResult = new Object[size];
            int startN = 0;
            for(Map.Entry<String,Object> entity:map.entrySet()){
                //第一次拼接，只拼接一次
                if(firstFlag){
                    sb.append(entity.getKey()).append(" ,");
                    sbValue.append("? ,");
                }
                //将值放到单个数据组中
                rowResult[startN] = entity.getValue();
                startN++;
            }
            //将单条数据存放到集合中
            addResultList.add(rowResult);
            if(firstFlag){
                //第一次执行完后终止，下次不在执行
                firstFlag = false;
                sb.deleteCharAt(sb.length() - 1).append(" ) values (");
                sbValue.deleteCharAt(sbValue.length() - 1).append(" )");
            }

        }
        //拼接后的结果 insert into table_name(text1,text2,text3) values (?,?,?)
        sb.append(sbValue.toString());
        log.info("sql: " + sb);
        //sql,List<Object[]>
        jdbcTemplate.batchUpdate(sb.toString(), addResultList);

    }

    @Resource
    private PageUtil pageUtil;

    /**
     * 分页查询
     */
    @ApiOperation("分页查询")
    @PostMapping("/getData")
    public PageVo getData(SearchParam searchParam){
        ArrayList params = new ArrayList();
        String sql = "select * from SEC_FAULT_DIAGNOSIS where 1=1 ";
//        if (searchParam.getName() != null) {
//            sql += " and name like '%?%'";
//            params.add(searchParam.getName());
//        }
        if (searchParam.getCreateTime() != null) {
            sql += " and create_Time >= ?";
            params.add(searchParam.getCreateTime());
        }
        sql += " order by \"ID\" desc";

        Integer pageNum = searchParam.getPageNum();
        Integer pageSize = searchParam.getPageSize();
        PageVo res = pageUtil.queryForPage(sql, pageNum, pageSize, params.toArray());
        return res;

    }



}
