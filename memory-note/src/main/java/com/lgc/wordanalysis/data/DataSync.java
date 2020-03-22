package com.lgc.wordanalysis.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.baselibrary.utils.TimeUtil;
import com.lgc.wordanalysis.base.WordAnalysisApplication;
import com.lgc.wordanalysis.user.setting.SettingActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public static void importFromTxt(Context context, String importPath) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(importPath)), "UTF-8")
            );
            // Toast.makeText(context, "开始导入", Toast.LENGTH_SHORT).show();
            Logcat.e("开始导入");
            String line;
            List<String> jStringList = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                jStringList.add(line);
            }
            GlobalData.getInstance().importFromJStringList(jStringList);
        } catch (IOException e) {
            e.printStackTrace();
            // Toast.makeText(context, "IO出错", Toast.LENGTH_LONG).show();
        }
    }

    public static void importFromCsv(Context context, String importPath) {
        try {
            File excel = new File("E:\\本人的项目\\单词笔记项目资源\\单词库\\高中词汇表-3500.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(excel), "gbk"));
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                Log.d("DataSync", line);
                String[] items = line.split("\t");
                Word word = new Word();

                word.setMeaningList(getMeaningList(items[1]));
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static List<Word.WordMeaning> getMeaningList(String item) {
        List<Word.WordMeaning> meaningList = new ArrayList<>();
        String[] meaningNameList = item.split(";");
        Word.WordMeaning oneMeaning = new Word.WordMeaning();
        for (String oneName : meaningNameList) {
            oneMeaning.setMeaning(oneName);
        }
        meaningList.add(oneMeaning);
        return meaningList;
    }

    public static String exportData2Sd(Context context, String exportDataName) {
        exportDataName = exportDataName + "_" + TimeUtil.curTimeToString() + ".txt";
        List<Word> allWord = GlobalData.getInstance().getAllWord();

        String outPath = WordAnalysisApplication.DEFAULT_FIL_PATH + File.separator + exportDataName;
        try {
            new File(WordAnalysisApplication.DEFAULT_FIL_PATH).mkdirs();
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

        String outPath = WordAnalysisApplication.DEFAULT_FIL_PATH + File.separator + exportDataName;
        try {
            new File(WordAnalysisApplication.DEFAULT_FIL_PATH).mkdirs();
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
            Toast.makeText(context, "导出完成" + "路径 = " + outPath, Toast.LENGTH_LONG).show();
            return outPath;
        } catch (IOException e) {
            Toast.makeText(context, "IO出错", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void importFromStandardCsv(Context context, InputStream importStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(importStream, "gbk"));
            String line = br.readLine();
            while (line != null) {
                line = br.readLine();
                Log.d("DataSync", line);
                int id = line.indexOf(',');
                Word word = new Word();
                String name  = line.substring(0, id);
                word.setName(name);

                String meaning = line.substring(id+1);
                word.setInputMeaning(meaning);
                word.setMeaningList(getMeaningList(meaning));
                Log.d("DataSync", word.toString());
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
