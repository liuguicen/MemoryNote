package com.lgc.memorynote.data;

import org.json.JSONArray;
import com.google.gson.Gson;
import com.lgc.memorynote.base.Logcat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/01
 *      version : 1.0
 * <pre>
 */

public class GlobalWordData {
    private static List<Word> mAllWord = new ArrayList<>();
    private static GlobalWordData mInstance = new GlobalWordData();
    private GlobalWordData() {
        Logcat.d(System.currentTimeMillis());
        MyDatabase database = MyDatabase.getInstance( MyApplication.appContext);
        List<String>  jasonList = new ArrayList<>();
        try {
            database.queryAllWord(jasonList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
        Gson gson = new Gson();
        for (String oneJsonWord : jasonList) {
            mAllWord.add(gson.fromJson(oneJsonWord, Word.class));
        }
        Logcat.d(System.currentTimeMillis());
    }
    public static GlobalWordData getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalWordData();
        }
        return mInstance;
    }

    public List<Word> getAllWord() {
        return mAllWord;
    }
}
