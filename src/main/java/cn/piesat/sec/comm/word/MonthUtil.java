package cn.piesat.sec.comm.word;

import cn.piesat.sec.comm.constant.Constant;
import cn.piesat.sec.comm.word.domain.MonthBean;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.CellStyle;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthUtil {
    private static final Logger logger = LoggerFactory.getLogger(MonthUtil.class);

    /**
     * 根据数据生成二维表格对象集合
     *
     * @param data 表格数据
     * @return 表格数据对象集合
     */
    public static List<RowRenderData> createTableData(String[][] data) {
        List<RowRenderData> rows = new ArrayList<>();
        if (data.length > 0) {
            CellStyle cellStyle = new CellStyle();
            cellStyle.setVertAlign(XWPFTableCell.XWPFVertAlign.CENTER); // 单元格数据垂直居中
            for (int i = 0; i < data.length; i++) {
                List<CellRenderData> cells = new ArrayList<>();
                for (int j = 0; j < data[i].length; j++) {
                    CellRenderData cell = new CellRenderData();
                    List<ParagraphRenderData> pfcell = new ArrayList<>();
                    ParagraphRenderData prd = new ParagraphRenderData();
                    List<RenderData> contents = new ArrayList<>();
                    TextRenderData trd = new TextRenderData();
                    String value = data[i][j];
                    value = value == null ? "" : value;
                    trd.setText(value);
                    contents.add(trd);
                    prd.setContents(contents);
                    pfcell.add(prd);
                    cell.setParagraphs(pfcell);
                    cell.setCellStyle(cellStyle);
                    cells.add(cell);
                }
                RowRenderData rowRenderData = Rows.of().textFontSize(Constant.TB_FONT_SMALL_SIZE).rowExactHeight(Constant.TB_ROW_HEIGHT).horizontalCenter().verticalCenter().create();
                rowRenderData.setCells(cells);
                rows.add(rowRenderData);
            }
        }
        return rows;
    }

    /**
     * 写出日报详情.doc文件
     *
     * @param model     日报详情模板文件
     * @param tarPath   生成目标位置
     * @param monthBean 数据对象
     */
    public static void createDailyMonthDocx(InputStream model, String tarPath, MonthBean monthBean) {
        try (XWPFTemplate template = XWPFTemplate.compile(model);) {
            template.render(monthBean);
            template.writeAndClose(new FileOutputStream(tarPath));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Writing docx throws exception --- %s", e.getMessage()));
        }
    }
}
