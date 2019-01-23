package com.lgc.wordanalysis.data;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.baselibrary.utils.TimeUtil;
import com.lgc.wordanalysis.base.MemoryNoteApplication;
import com.xuxueli.poi.excel.ExcelExportUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/09/26
 *      version : 1.0
 * <pre>
 */

public class DataSync {

    public static void importFromSD(Context context, String importPath) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                       new FileInputStream(new File(importPath)), "UTF-8")
            );
            Toast.makeText(context, "开始导入", Toast.LENGTH_SHORT).show();
            Logcat.e("开始导入");
            String line;
            List<String> jStringList = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                jStringList.add(line);
            }
            GlobalData.getInstance().importFromJStringList(jStringList);
            Toast.makeText(context, "导入完成", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "IO出错", Toast.LENGTH_LONG).show();
        }
    }


    public static String exportData2Sd(Context context, String exportDataName) {
        exportDataName = exportDataName + "_" + TimeUtil.curTimeToString() + ".txt";
        List<Word> allWord = GlobalData.getInstance().getAllWord();

        String outPath = MemoryNoteApplication.DEFAULT_FIL_PATH +  File.separator + exportDataName;
        try {
            new File(MemoryNoteApplication.DEFAULT_FIL_PATH).mkdirs();
            File dataFile = new File(outPath);
            if (dataFile.exists()) {
                if (!dataFile.delete()) {
                    throw new IOException();
                }
            }

            if (!dataFile.createNewFile()) {
                throw new IOException();
            }

            FileOutputStream fo = new FileOutputStream(new File(outPath));
            OutputStreamWriter ps = new OutputStreamWriter(fo, "UTF-8");
            Toast.makeText(context, "开始导出", Toast.LENGTH_SHORT).show();
            Gson gson = new Gson();
            for (Word word : allWord) {
                String data = gson.toJson(word);
                ps.write(data + "\n");
            }
            ps.flush();
            Toast.makeText(context, "导出完成" + "路径 = " + outPath, Toast.LENGTH_LONG).show();
            return outPath;
        } catch (IOException e) {
            Toast.makeText(context, "IO出错", Toast.LENGTH_LONG).show();
            return null;
        }

    }

    public static String exportExcel2Sd(Context context, String exportDataName) {
        exportDataName = exportDataName + "_" + TimeUtil.curTimeToString() + ".txt";
        List<Word> allWord = GlobalData.getInstance().getAllWord();

        String outPath = MemoryNoteApplication.DEFAULT_FIL_PATH +  File.separator + exportDataName;
        try {
            new File(MemoryNoteApplication.DEFAULT_FIL_PATH).mkdirs();
            File dataFile = new File(outPath);
            if (dataFile.exists()) {
                if (!dataFile.delete()) {
                    throw new IOException();
                }
            }

            if (!dataFile.createNewFile()) {
                throw new IOException();
            }
            //FileOutputStream fo = new FileOutputStream(new File(outPath));
           // OutputStreamWriter ps = new OutputStreamWriter(fo, "UTF-8");
            Toast.makeText(context, "开始导出", Toast.LENGTH_SHORT).show();
            //Gson gson = new Gson();
            //for (Word word : allWord) {
            //    String data = gson.toJson(word);
            //    ps.write(data + "\n");
           // }
           // ps.flush();
            ExcelExportUtil.exportToFile( allWord, outPath);
            Toast.makeText(context, "导出完成" + "路径 = " + outPath, Toast.LENGTH_LONG).show();
            return outPath;
        } catch (IOException e) {
            Toast.makeText(context, "IO出错", Toast.LENGTH_LONG).show();
            return null;
        }
    }

}