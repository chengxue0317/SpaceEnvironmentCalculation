package cn.piesat.sec.comm.word;

import cn.piesat.sec.comm.constant.Constant;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.CellStyle;
import com.deepoove.poi.data.style.TableStyle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommonWordUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonWordUtil.class);

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
                    trd.setText(data[i][j]);
                    contents.add(trd);
                    prd.setContents(contents);
                    pfcell.add(prd);
                    cell.setParagraphs(pfcell);
                    cell.setCellStyle(cellStyle);
                    cells.add(cell);
                }
                // 表头居中、加粗
                if (i == 0) {
                    RowRenderData rowRenderData = Rows.of().textFontFamily("黑体").textFontSize(Constant.TB_FONT_SIZE).rowExactHeight(Constant.TB_ROW_HEIGHT).horizontalCenter().verticalCenter().create();
                    rowRenderData.setCells(cells);
                    rows.add(rowRenderData);
                }
                // 其它表行数据居中居对齐
                else {
                    RowRenderData rowRenderData = Rows.of().textFontFamily("仿宋").textFontSize(Constant.TB_FONT_SIZE).rowExactHeight(Constant.TB_ROW_HEIGHT).horizontalCenter().verticalCenter().create();
                    rowRenderData.setCells(cells);
                    rows.add(rowRenderData);
                }
            }
        }
        return rows;
    }

    /**
     * 动态绘制表格图片
     *
     * @param dataList 图片数据集合
     * @param cols     每行显示表格列数
     * @param width    图片宽度
     * @param height   图片高度
     * @return 表格图片数据
     */
    public static TableRenderData createTablePics(List<String> dataList, int cols, int width, int height) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new TableRenderData();
        }
        int len = dataList.size() / cols;
        int rowLen = dataList.size() % cols == 0 ? len : len + 1; // 要显示多少行表格
        TableStyle.BorderStyle bdstyle = new TableStyle.BorderStyle();
        bdstyle.setType(XWPFTable.XWPFBorderType.DOTTED); // 表格边线样式
        TableRenderData picTB = Tables.ofA4Width().border(bdstyle).center().create();
        List<RowRenderData> rows1 = new ArrayList<>();
        CellStyle cellStyle = new CellStyle();
        cellStyle.setVertAlign(XWPFTableCell.XWPFVertAlign.CENTER); // 单元格数据垂直居中
        if (dataList.size() > 0) {
            for (int i = 0; i < rowLen; i++) {
                List<CellRenderData> cells = new ArrayList<>();
                for (int j = 0; j < cols; j++) {
                    int idx = cols * i + j;
                    if (idx < dataList.size()) {
                        CellRenderData cell = new CellRenderData();
                        List<ParagraphRenderData> centCell = new ArrayList<>();
                        ParagraphRenderData prd = new ParagraphRenderData();
                        List<RenderData> contents = new ArrayList<>();
                        String item = dataList.get(idx);
                        String lowItem = item.toLowerCase();
                        if (CommonWordUtil.isPicture(lowItem)) {
                            contents.add(new PictureRenderData(width, height, item));
                        } else {
                            contents.add(new TextRenderData(item));
                        }
                        prd.setContents(contents);
                        centCell.add(prd);
                        cell.setParagraphs(centCell);
                        cell.setCellStyle(cellStyle);
                        cells.add(cell);
                    } else {
                        cells.add(new CellRenderData());
                    }
                }
                RowRenderData rowRenderData = Rows.of().textFontSize(Constant.TB_FONT_SIZE).horizontalCenter().verticalCenter().create();
                rowRenderData.setCells(cells);
                rows1.add(rowRenderData);
            }
        }
        picTB.setRows(rows1);
        return picTB;
    }

    /**
     * 判断传入的字符串是否是图片文件
     *
     * @param lowItem 图片文件路径小写
     * @return 是否是图片文件后缀
     */
    public static boolean isPicture(String lowItem) {
        return lowItem.endsWith(".png")
                || lowItem.endsWith(".jpg")
                || lowItem.endsWith(".jpeg")
                || lowItem.endsWith(".gif")
                || lowItem.endsWith(".emf")
                || lowItem.endsWith(".wmf")
                || lowItem.endsWith(".pict")
                || lowItem.endsWith(".dib")
                || lowItem.endsWith(".tiff")
                || lowItem.endsWith(".eps")
                || lowItem.endsWith(".bmp")
                || lowItem.endsWith(".wpg");
    }
}
