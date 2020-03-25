package com.lgc.wordanalysis.data;

import android.content.Context;
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.lgc.baselibrary.UIWidgets.ProgressCallback;
import com.lgc.baselibrary.utils.Logcat;
import com.lgc.baselibrary.utils.TimeUtil;
import com.lgc.wordanalysis.base.Util;
import com.lgc.wordanalysis.base.WordAnalysisApplication;
import com.lgc.wordanalysis.base.WordDisplayAnalyzer;

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
import java.util.regex.Pattern;

import okhttp3.internal.Internal;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/09/26
 *      version : 1.0
 * <pre>
 */

public class DataSync {
    public static final boolean isTest = false;

    public static int importFromTxt(Context context, String importPath, boolean isDelete,
                                    boolean isReplace, ProgressCallback progressCallback) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(importPath)), "UTF-8")
            );

            if (progressCallback != null)
                progressCallback.msg("开始导入");
            ld("开始导入");
            String line;
            List<String> jStringList = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                jStringList.add(line);
            }
            GlobalData.getInstance().importFromJStringList(jStringList, null, true);
        } catch (IOException e) {
            e.printStackTrace();

            if (progressCallback != null)
                progressCallback.msg("文件读写出错");
            return -1;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
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

    /**
     * 导出格式-列名称
     * 单词名  单词意思  单词陌生度   近似词列表  词组  近义词   记忆方法  上次记忆时间
     * 应对后面的变化，要求用户必须按照这样的顺序排列单词表，否则出错
     * 目前来看用程序应对没想到好的方式
     */
    public static void importFromStandardCsv(Context context, InputStream importStream, String
            encodingWay, boolean isDelete, boolean isReplace,
                                             ProgressCallback progressCallback) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(importStream, encodingWay));
            List<Word> wordList = new ArrayList<>();
            String line = br.readLine();
            if (line != null) { // 第一行为title, 跳过
                line = br.readLine();
            }
            int i = 1;
            while (line != null) {
                Log.d("DataSync", (i++) + line);
                Word word = new Word();

                List<String> gridList = decodeCsvLine(line);
                // 单词名
                word.setName(gridList.get(0));
                try { // 只要名字有了，其它地方解析出错可不管

                    String meaning = gridList.get(1);
                    word.setInputMeaning(meaning);
                    word.setMeaningList(getMeaningList(meaning)); // 词义
                    word.setStrangeDegreeByDisplay(gridList.get(2)); // 陌生度
                    word.setSimilarByDisplay(gridList.get(3)); // 相似单词
                    word.setGroupByDisplay(gridList.get(4));  // 单词组
                    word.setSynonymByDisplay(gridList.get(5)); // 近义词
                    word.setInputRememberWay(gridList.get(6));  // 记忆方法
                    word.setLastRememberTimeByDisplay(gridList.get(7)); // 上次记忆时间


                    Log.d("DataSync", word.toString());
                    word.setLastModifyTime(System.currentTimeMillis());
                } catch (Exception e) {
//                    e.printStackTrace();
                }

                wordList.add(word);
                line = br.readLine();
            }
            GlobalData.getInstance().importFromWordList(wordList, progressCallback, isDelete, isReplace);

            if (progressCallback != null)
                progressCallback.msg("单词导入完成");
        } catch (Exception e) {

            if (progressCallback != null)
                progressCallback.msg("导入出错" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 导出为csv文件
     * 导出格式-列名称
     * 单词名  单词意思  单词陌生度   近似词列表  词组  近义词   记忆方法  上次记忆时间
     */
    public static String exportAsCsv(Context context, String exportDataName, ProgressCallback progressCallback) {
        exportDataName = exportDataName + "_" + TimeUtil.curTimeToString() + ".csv";
        String outPath = WordAnalysisApplication.DEFAULT_FIL_PATH + File.separator + exportDataName;
        List<Word> allWord = GlobalData.getInstance().getAllWord();
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

            writeWordCsvTitle(ps);
            for (Word word : allWord) {
                writeOneWordAsCsvLine(word, ps);
            }
            ps.flush();
            if (progressCallback != null)
                progressCallback.msg("导出完成" + "路径 = " + outPath);
            return outPath;
        } catch (IOException e) {
            if (progressCallback != null)
                progressCallback.msg("IO出错");
            return null;
        }
    }

    /**
     * 导出格式-列名称
     * 单词名  单词意思  单词陌生度   近似词列表  词组  近义词   记忆方法  上次记忆时间
     * 应对后面的变化，要求用户必须按照这样的顺序排列单词表，否则出错
     * 目前来看用程序应对没想到好的方式
     */
    private static void writeWordCsvTitle(OutputStreamWriter ps) throws IOException {
        ps.write("单词名,单词意思,单词陌生度,近似词列表,词组,近义词,记忆方法,上次记忆时间\n");
    }

    /**
     * 导出格式-列名称
     * 单词名  单词意思  单词陌生度   近似词列表  词组  近义词   记忆方法  上次记忆时间
     */
    private static void writeOneWordAsCsvLine(Word word, OutputStreamWriter ps) throws
            IOException {
        ld(word);
        String grid = encode2CsvGrid(word.name);
        grid += ",";
        grid += encode2CsvGrid(word.inputMeaning);
        grid += ",";
        grid += encode2CsvGrid(word.strangeDegree + "");
        grid += ",";
        grid += encode2CsvGrid(word.inputSimilarWords);
        grid += ",";
        grid += encode2CsvGrid(word.inputWordGroup);
        grid += ",";
        grid += encode2CsvGrid(word.inputSynonyms);
        grid += ",";
        grid += encode2CsvGrid(word.inputRememberWay);
        grid += ",";
        grid += encode2CsvGrid(word.getLastRememberTimeDisplay());
        grid += '\n';
        ld("转换成csv的word = \n" + grid);
        ps.write(grid);
    }

    private static void ld(Object msg) {
        if (isTest) {
            Log.d("DataSync", msg == null ? "null" : msg.toString());
        }
    }

    public static String export2JsonTxt(Context context, String exportDataName) {
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
            Gson gson = new Gson();
            for (Word word : allWord) {
                String data = gson.toJson(word);
                ps.write(data + "\n");
            }
            ps.flush();
            return outPath;
        } catch (IOException e) {
            return null;
        }

    }

    public static String exportExcel2Sd(Context context, String exportDataName, ProgressCallback progressCallback) {
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

            if (progressCallback != null)
                progressCallback.msg("开始导出");
            //Gson gson = new Gson();
            //for (Word word : allWord) {
            //    String data = gson.toJson(word);
            //    ps.write(data + "\n");
            // }
            // ps.flush();

            if (progressCallback != null)
                progressCallback.msg("导出完成" + "路径 = " + outPath);
            return outPath;
        } catch (IOException e) {

            if (progressCallback != null)
                progressCallback.msg("IO出错");
            return null;
        }
    }

    /**
     * 一般内容转换成csv的一格
     *
     * @param content 要转换成csv文件中一格的内容
     */
    public static String encode2CsvGrid(@Nullable String content) {
        // first convert " to ""
        if (content == null) {
            content = "";
        }
        ld(content);
        content = content.replaceAll("\"", "\"\"");

        // second, if content contain the special char, enclose the whole content in ""
        if (Pattern.compile("[,\"\n]").matcher(content).find()) {
            content = "\"" + content + "\"";
        }
        ld(content);
        return content;
    }

    /**
     * @param content csv文件中的一格,需保证是存在的一格
     */
    @NonNull
    public static String decodeGridOfCsv(@Nullable String content) {
        ld(content);
        if (content == null)
            return "";
        if (content.length() <= 1) return content;
        // 如有特殊字符，长度至少为3
        // 先去掉两端的引号
        if (content.charAt(0) == '"' && content.charAt(content.length() - 1) == '"') {
            content = content.substring(1, content.length() - 1);
        }
        // 再去掉中间的转义字符
        content = content.replaceAll("\"(.)", "$1");
        ld(content);
        return content;
    }

    /**
     * @return 返回的字串符列表中不存在空
     */
    @NonNull
    public static List<String> decodeCsvLine(String line) {
        List<String> gridList = new ArrayList<>();
        if (line == null) {
            return gridList;
        }
        ld(line);
        int lastID = 0;
        for (int i = 0; i < line.length(); i++) {
            // 逗号分隔符，但是又不是",，说明遇到了一个的结束
            if (line.charAt(i) == ',' && i > 0 && line.charAt(i - 1) != '\"') {
                String grid = line.substring(lastID, i);
                grid = decodeGridOfCsv(grid);
                gridList.add(grid); // lastID == i 时返回空字符串"", 符合要求
                ld(grid);
                lastID = i + 1; // 单元格不为空时，lastID指向第一个字符，为空时，指向分隔符",", 下次找到分隔符id = lastID,能有效处理
            }
        }
        // csv的文件中，最后一格后面没有逗号， 所以加上最后一格的内容, lastID = 0, line.length = 0 也有效
        String grid = decodeGridOfCsv(line.substring(lastID));
        gridList.add(grid);
        ld(grid);
        return gridList;
    }
}
