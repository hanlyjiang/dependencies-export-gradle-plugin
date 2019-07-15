package cn.hanlyjiang.gradle.analysis;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author hanlyjiang on 2019/7/15 13:55
 * @version 1.0
 */
public class WorkBookUtils {

    /**
     * create workbook
     *
     * @param newFile newFile
     * @return
     * @throws IOException
     * @throws BiffException
     */
    static WritableWorkbook getWorkSheet(String newFile) {
        File workFileNew = new File(newFile);
        if (workFileNew.exists()) {
            workFileNew.delete();
        } else {
            workFileNew.getParentFile().mkdirs();
        }
        try {
            workFileNew.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WritableWorkbook workbook = null;
        try {
            workbook = Workbook.createWorkbook(new File(newFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }
}
