package com.lgc.memorynote.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.base.MemoryNoteApplication;
import com.lgc.memorynote.wordDetail.Word;
import com.lgc.memorynote.wordList.Command;

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

public class GlobalData {
    private static List<Word> mAllWord = new ArrayList<>();
    private static GlobalData mInstance = new GlobalData();
    private List<String> mCommandList;
    private static final String SP_COMMAND_LIST = "command_string";

    private GlobalData() {
        Logcat.d(System.currentTimeMillis());
        MyDatabase database = MyDatabase.getInstance();
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

        getCommandList();

        Logcat.d(System.currentTimeMillis());
    }
    public static GlobalData getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalData();
        }
        return mInstance;
    }

    public List<Word> getAllWord() {
        return mAllWord;
    }

    public void updateWord(com.lgc.memorynote.wordDetail.Word word) {
        try {
            MyDatabase.getInstance().insertWord(word.getWord(), new Gson().toJson(word));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public void deleteWord(Word word) {
        try {
            MyDatabase.getInstance().deleteWord(word.getWord());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public List<String> getCommandList() {
        if (mCommandList == null) {
            SharedPreferences sp = MemoryNoteApplication.appContext.getSharedPreferences("user_habit", Context.MODE_PRIVATE);
            String jsonCommand = sp.getString(SP_COMMAND_LIST, "");
            if (jsonCommand.isEmpty()) {
                mCommandList = Command.commadList;
            } else {
                mCommandList = new Gson().fromJson(jsonCommand,  new TypeToken<List<String>>(){}.getType());
            }
        }
        return mCommandList;
    }

    public void updateCommandSort(List<String> inputCommands) {
        mCommandList.removeAll(inputCommands);
        mCommandList.addAll(0, inputCommands);
    }

    public void saveCommandList() {
        SharedPreferences sp = MemoryNoteApplication.appContext.getSharedPreferences("user_habit", Context.MODE_PRIVATE);
        sp.edit().putString(SP_COMMAND_LIST, new Gson().toJson(mCommandList));
    }
}
