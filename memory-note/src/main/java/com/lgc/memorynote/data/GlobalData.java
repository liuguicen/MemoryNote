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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/01
 *      version : 1.0
 * <pre>
 */

public class GlobalData {
    // 涉及到数据同步的问题，比较麻烦，两个基本上在一起操作
    private static List<Word> mAllWords = new ArrayList<>();
    private static List<Word> mCurWords = new ArrayList<>();

    private static GlobalData mInstance = new GlobalData();
    private List<String> mCommandList;
    private Map<String, String> mUIComandMap = new HashMap<>();
    private static final String SP_COMMAND_LIST = "command_string";

    private GlobalData() {
        Logcat.d(System.currentTimeMillis());
        queryAllWord();
        getCommandList();

        Logcat.d(System.currentTimeMillis());
    }

    private void queryAllWord() {
        MyDatabase database = MyDatabase.getInstance();
        List<String>  jasonList = new ArrayList<>();
        try {
            database.queryAllWord(jasonList);
            Gson gson = new Gson();
            for (String oneJsonWord : jasonList) {
                Word word = gson.fromJson(oneJsonWord, Word.class);
                mAllWords.add(word);
                mCurWords.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    public static GlobalData getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalData();
        }
        return mInstance;
    }

    public List<Word> getAllWord() {
        if (mAllWords == null) {
            queryAllWord();
        }
        return mAllWords;
    }

    public List<Word> getCurWords() {
        if (mAllWords == null) {
            queryAllWord();
        }
        return mCurWords;
    }

    public void setCurWords(List<Word> curWords) {
        mCurWords = curWords;
    }

    public void addWord(Word word) {
        try {
            MyDatabase.getInstance().insertWord(word.getName(), new Gson().toJson(word));
            mAllWords.add(word);
            mCurWords.add(word);
        } catch (IOException e) {
            e.printStackTrace();
            Logcat.d(e.getMessage());
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public void updateWord(Word word) {
        try {
            MyDatabase.getInstance().insertWord(word.getName(), new Gson().toJson(word));
            // 更新，内存中的已经更新了，不用在更新
        } catch (IOException e) {
            e.printStackTrace();
            Logcat.d(e.getMessage());
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public void deleteWord(Word word) {
        try {
            MyDatabase.getInstance().deleteWord(word.getName());
            mAllWords.remove(word);
            mCurWords.remove(word);
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
                mCommandList = Command.commandList;
            } else {
                mCommandList = new Gson().fromJson(jsonCommand,  new TypeToken<List<String>>(){}.getType());
            }

        }
        return mCommandList;
    }

    public Map<String, String> getUIComandMap() {
        return Command.UICommandMap;
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
