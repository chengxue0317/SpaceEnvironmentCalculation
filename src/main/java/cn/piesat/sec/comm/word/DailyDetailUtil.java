package cn.piesat.sec.comm.word;

import cn.piesat.sec.comm.word.domain.DailyDetailBean;
import com.deepoove.poi.XWPFTemplate;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class DailyDetailUtil {
    private static final Logger logger = LoggerFactory.getLogger(DailyDetailUtil.class);

    /**
     * 写出日报详情.doc文件
     *
     * @param model           日报详情模板文件
     * @param tarPath         生成目标位置
     * @param dailyDetailBean 数据对象
     */
    public static void createDailyDetailDocx(InputStream model, String tarPath, DailyDetailBean dailyDetailBean) {
        // 如果文件已经存在则删除
        File tarFile = FileUtils.getFile(tarPath);
        if(tarFile.exists()){
            try {
                FileUtils.forceDelete(tarFile);
            } catch (IOException e) {
                logger.error(String.format(Locale.ROOT, "---------Delete existing target files  %s", e.getMessage()));
            }
        }

        try (XWPFTemplate template = XWPFTemplate.compile(model)){
            template.render(dailyDetailBean);
            template.writeAndClose(new FileOutputStream(tarPath));
        } catch (IOException e) {
            logger.error(String.format(Locale.ROOT, "Writing docx throws exception --- %s", e.getMessage()));
        }
    }
}
