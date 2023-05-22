package cn.piesat.sec.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.piesat.kjyy.common.log.annotation.OpLog;
import cn.piesat.kjyy.common.log.enums.BusinessType;
import cn.piesat.kjyy.common.web.annotation.validator.group.AddGroup;
import cn.piesat.kjyy.common.web.annotation.validator.group.UpdateGroup;
import cn.piesat.kjyy.core.model.dto.PageBean;
import cn.piesat.kjyy.core.model.vo.PageResult;
import cn.piesat.sec.model.entity.FieldManageDO;
import cn.piesat.sec.model.vo.FaultDiagnosisM2VO;
import cn.piesat.sec.model.vo.PageVo;
import cn.piesat.sec.model.vo.SearchParam;
import cn.piesat.sec.service.FieldManageService;
import cn.piesat.sec.utils.ExecUtil;
import cn.piesat.sec.utils.POIUtils;
import cn.piesat.sec.utils.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

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

    @Value("${python.path.fault_diagnosis}")
    private String faultDiagnosis;

    @Value("${python.path.fault_diagnosis_m2}")
    private String faultDiagnosisM2;

    @Value("${python.data.model}")
    private String model;

    @Value("${python.path.config}")
    private String pythonConfig;

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
     * 获取特征量
     */
    @ApiOperation("获取特征量")
    @OpLog(op = BusinessType.OTHER, description = "获取特征量")
    @GetMapping("/getFeatureData")
    public List<FieldManageDO> getFeatureData(){
        QueryWrapper<FieldManageDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("FIELD_NAME")
                .ne("FIELD_NAME","ID")
                .ne("FIELD_NAME","CREATE_TIME")
                .ne("FIELD_NAME","Error_ID");
        return fieldManageService.list(queryWrapper);

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
    @OpLog(op = BusinessType.OTHER, description = "新增字段")
    @PostMapping("/addField")
    @Transactional
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
            sqlBuilder.append("default (");
            sqlBuilder.append(fieldManageDO.getDefaultValue());
            sqlBuilder.append(") not null ");
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

        fieldManageDO.setCreateTime(LocalDateTime.now());
        fieldManageService.add(fieldManageDO);
    }


    /**
     * 修改字段名称
     */
    @ApiOperation("修改字段名称")
    @OpLog(op = BusinessType.OTHER, description = "修改字段名称")
    @PostMapping("/changeFieldName")
    @Transactional
    public void changeFieldName(@RequestParam("id") Long id,
                                @RequestParam("oldFieldName") String oldFieldName,
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

        FieldManageDO fieldManageDO =new FieldManageDO();
        fieldManageDO.setId(id);
        fieldManageDO.setFieldName(newFieldName);
        fieldManageService.update(fieldManageDO);
    }


    /**
     * 修改字段类型
     */
    @ApiOperation("修改字段类型")
    @OpLog(op = BusinessType.OTHER, description = "修改字段类型")
    @PostMapping("/changeDataType")
    @Transactional
    public void changeDataType(@RequestParam("id") Long id,
                               @RequestParam("fieldName") String fieldName,
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

        FieldManageDO fieldManageDO =new FieldManageDO();
        fieldManageDO.setId(id);
        fieldManageDO.setDataType(dataType);
        fieldManageService.update(fieldManageDO);
    }


    /**
     * 修改约束
     */
    @ApiOperation("修改约束")
    @OpLog(op = BusinessType.OTHER, description = "修改约束")
    @PostMapping("/changeConstraint")
    @Transactional
    public void changeConstraint(@RequestParam("id") Long id,
                                 @RequestParam("oldFieldName") String oldFieldName,
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

        FieldManageDO fieldManageDO =new FieldManageDO();
        fieldManageDO.setId(id);
        fieldManageDO.setNotNullConstraint(notNullConstraint);
        fieldManageService.update(fieldManageDO);
    }


    /**
     * 修改精度
     */
    @ApiOperation("修改精度")
    @OpLog(op = BusinessType.OTHER, description = "修改精度")
    @PostMapping("/changePrecision")
    @Transactional
    public void changePrecision(@RequestParam("id") Long id,
                                @RequestParam("fieldName") String fieldName,
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

        FieldManageDO fieldManageDO =new FieldManageDO();
        fieldManageDO.setId(id);
        fieldManageDO.setPrecision(precision);
        fieldManageService.update(fieldManageDO);
    }

    /**
     * 修改注释
     */
    @ApiOperation("修改注释")
    @OpLog(op = BusinessType.OTHER, description = "修改注释")
    @PostMapping("/changeAnnotation")
    @Transactional
    public void changeAnnotation(@RequestParam("id") Long id,
                                @RequestParam("fieldName") String fieldName,
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

        FieldManageDO fieldManageDO =new FieldManageDO();
        fieldManageDO.setId(id);
        fieldManageDO.setAnnotation(annotation);
        fieldManageService.update(fieldManageDO);
    }

    /**
     * 删除字段
     */
    @ApiOperation("删除字段")
    @OpLog(op = BusinessType.OTHER, description = "删除字段")
    @PostMapping("/delField")
    @Transactional
    public void delField(@RequestParam("id") Long id,
                        @RequestParam("fieldName") String fieldName){
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

        fieldManageService.delete(id);
    }

    /**
     * 插入数据
     */
    @ApiOperation("插入数据")
    @OpLog(op = BusinessType.OTHER, description = "插入数据")
    @PostMapping("/insertData")
    public void insertData(@RequestBody List<Map<String,Object>> dataList){
        String tableName = "SEC_FAULT_DIAGNOSIS";
        batchInsertList(dataList,tableName);

    }


    public void batchInsertList(List<Map<String,Object>> dataList,String tableName){
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
    @OpLog(op = BusinessType.OTHER, description = "分页查询")
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


    /**
     * 导出模板
     */
    @ApiOperation("导出模板")
    @OpLog(op = BusinessType.OTHER, description = "导出模板")
    @PostMapping("/exportMode")
    public void exportMode(HttpServletResponse response){
        List<Map<String, Object>> data = jdbcTemplate.queryForList("select * from SEC_FAULT_DIAGNOSIS ");
        exportExcel(response,data);
    }

    private void exportExcel(HttpServletResponse response, List<Map<String, Object>> list) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        //表头
        XSSFRow headRow = sheet.createRow(0);
        //取List的第一个元素进行遍历，找出所有key作为表头
        Iterator<Map.Entry<String, Object>> iterator = list.get(0).entrySet().iterator();
        int i = 0;
        //用于数据行查找
        List<Object> headRowData = new ArrayList<>();
        iterator.next();
        while (iterator.hasNext()) {
            String next = iterator.next().getKey();
            //设置表头
            headRow.createCell(i).setCellValue(new XSSFRichTextString(next));
            headRowData.add(next);
            i++;
        }
        //列表
        int rowNUm = 1;
        for (Map<String, Object> dataMap : list) {
            //创建数据行
            XSSFRow dataRow = sheet.createRow(rowNUm);
            for (int j = 0, size = headRowData.size(); j < size; j++) {
                //设置数据
                System.out.println(dataMap.get(headRowData.get(j)));
                Object value = dataMap.get(headRowData.get(j));
                if (value!=null){
                    dataRow.createCell(j).setCellValue(value.toString());
                }

            }
            rowNUm++;
        }
        //数据导出
        response.setContentType("application/octet-stream;charset=UTF-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("excelName.xlsx", "UTF-8"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
        }
    }


    /**
     * 导入数据
     */
    @ApiOperation("导入数据")
    @OpLog(op = BusinessType.OTHER, description = "导入数据")
    @PostMapping("/importData")
    public void importData(@RequestParam("file") MultipartFile file){
        if (file != null && file.getSize() > 0) {
            //返回的第一条数据是表头信息
            try {
                List<Object[]> dataList = POIUtils.readExcel(file);
                StringBuilder sb = new StringBuilder(200);
                String tableName = "SEC_FAULT_DIAGNOSIS";
                sb.append("insert into ").append(tableName).append(" values(");
                for (int i=0;i<dataList.get(0).length;i++){
                    sb.append("?,");
                }
                sb.deleteCharAt(sb.length() - 1).append(" )");
                log.info("执行SQL语句----->{}", sb);
                dataList.remove(0);
                jdbcTemplate.batchUpdate(sb.toString(),dataList);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * 删除数据
     */
    @ApiOperation("删除数据")
    @OpLog(op = BusinessType.OTHER, description = "删除数据")
    @PostMapping("/delData")
    public void delData(@RequestBody List<Object[]> ids){
        String sql = "delete from SEC_FAULT_DIAGNOSIS where ID = ?";
        jdbcTemplate.batchUpdate(sql,ids);

    }


    @ApiOperation("卫星空间环境故障诊断M1")
    @OpLog(op = BusinessType.OTHER, description = "卫星空间环境故障诊断M1")
    @GetMapping("/faultDiagnosis")
    public String faultDiagnosis(String fields){
        String nowtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String command = "python3 "+faultDiagnosis+" \""+nowtime+"\" \""+pythonConfig+"\" \"SEC_FAULT_DIAGNOSIS\" \""+fields+"\" \"Error_ID\"";
//        String command = "python3 "+faultDiagnosis+" \""+nowtime+"\" \"/export/故障诊断多参数/xw.ini\" \"SEC_FAULT_DIAGNOSIS\" \""+fields+"\" \"Error_ID\"";
        log.info("执行Python命令：{}",command);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                String result = ExecUtil.execCmdWithResult(command);
                log.info("Python命令执行结果：{}",result);
            }
        }).start();

        return nowtime;

    }

    @ApiOperation("获取故障诊断M2模型路径")
    @OpLog(op = BusinessType.OTHER, description = "获取故障诊断M2模型路径")
    @GetMapping("/getModel")
    public List<String> getModel(String type){
        List<String> list = FileUtil.listFileNames(model.concat(type));
        return list;

    }

    @ApiOperation("获取故障诊断M2模型类型")
    @OpLog(op = BusinessType.OTHER, description = "卫星空间环境故障诊断M2")
    @GetMapping("/getModelType")
    public List<String> getModelType(){
        File[] files = FileUtil.ls(model);
        List<String> names = new ArrayList<>();
        for (int i=0;i<files.length;i++){
            names.add(files[i].getName());
        }
        return names;

    }

    @ApiOperation("卫星空间环境故障诊断M2")
    @OpLog(op = BusinessType.OTHER, description = "卫星空间环境故障诊断M2")
    @PostMapping("/faultDiagnosisM2")
    public String faultDiagnosisM2(@RequestBody FaultDiagnosisM2VO faultDiagnosisM2VO){
        String modelPath = model.concat(faultDiagnosisM2VO.getType()).concat(File.separator).concat(faultDiagnosisM2VO.getFileName());
        String command = "python3 "+faultDiagnosisM2+" "+modelPath+" "+Arrays.toString(faultDiagnosisM2VO.getData()).replaceAll("\\s*", "");
        log.info("执行Python命令：{}",command);
        String result = ExecUtil.execCmdWithResult(command);
        log.info("Python命令执行结果：{}",result);
        String jsonStr = StrUtil.subBetween(result, "###", "###");
        return jsonStr;

    }

}
