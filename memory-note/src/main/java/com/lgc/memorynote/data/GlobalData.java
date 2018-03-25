package com.lgc.memorynote.data;

import com.google.gson.Gson;
import com.lgc.memorynote.base.Logcat;
import com.lgc.memorynote.base.network.NetWorkUtil;
import com.lgc.memorynote.user.User;
import com.lgc.memorynote.wordList.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;

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

    private static List<String> recentCmdList;
    private User mUser;
    boolean mIsCheckedUser = false;

    // 静态内部类单例，比较好的用法
    public static final class H {
        public static final GlobalData instance = new GlobalData();
    }

    // 或者使用下面形式，再用getInstance方法访问
    //    private static class H {
    //        private static GlobalData instance = new GlobalData();
    //    }

    private GlobalData() {
        Logcat.e(System.currentTimeMillis());
        queryAllWord();
        mUser = User.getInstance();
        mUser.setName(SpUtil.getUserName());
        mUser.setPassword(SpUtil.getUserPassword());
        Logcat.e(System.currentTimeMillis());
    }

    public static void init() {
        getInstance();
    }

    private void queryAllWord() {
        MyDatabase database = MyDatabase.getInstance();
        List<String> jasonList = new ArrayList<>();
        try {
            database.queryAllWord(jasonList);
            Gson gson = new Gson();
            for (String oneJsonWord : jasonList) {
                Word word = gson.fromJson(oneJsonWord, Word.class);

                mAllWords.add(word);
                if (isShow(word)) {
                    mCurWords.add(word);
                }


                 /** 千万小心
                  * preProcess(word);

                  convertWordFromat(word);
                  * word数据结构变化的时候用， 把原版的Word拷一份出来了，命名为OldWord，去掉上面几行代码，
                 * 使用下面的代码，在oldWord2NewWord方法里面加上相关逻辑
                 *
                 *
                 * OldWord oldWord = gson.fromJson(oneJsonWord, OldWord.class);
                 * oldWord2NewWord(oldWord);
                 * 看效果加上这两行，不关掉应用可能会改到数据库
                 * mAllWords.add(word);
                 * mCurWords.add(word);
                 * Toast.makeText(MemoryNoteApplication.appContext, "应用数据已更新完毕", Toast.LENGTH_LONG).show();
                 */
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            database.close();
        }
    }

    private boolean isShow(Word word) {
        if (word.hasTag(Word.TAG_ROOT)) {
            return false;
        }
        return true;
    }

    /**
     * 直接转换 word的数据
     *
     * @param word
     */
    private void convertWordFromat(Word word) {
        /*
        List<Word.WordMeaning> meaningList = word.getMeaningList();
        if (meaningList != null) {
            for (Word.WordMeaning wordMeaning : meaningList) {
                wordMeaning.setTagList(null);
            }
        }
        updateWord(word, false);*/
    }

    private void preProcess(Word word) {
        List<Word.WordMeaning> meaningList = word.getMeaningList();
        if (meaningList != null) {
            for (Word.WordMeaning wordMeaning : meaningList) {
                if (wordMeaning.hasTags(Word.TAG_DI)) {
                    word.setStrangeDegree(Word.DEGREE_DI);
                } else if (wordMeaning.hasTags(Word.TAG_ROOT)) {
                    word.setStrangeDegree(Word.DEGREE_ROOT);
                } else if (wordMeaning.hasTags(Word.TAG_PREFFIX)) {
                    word.setStrangeDegree(Word.DEGREE_PREFFIX);
                } else if (wordMeaning.hasTags(Word.TAG_SUFFIX)) {
                    word.setStrangeDegree(Word.DEGREE_SUFFIX);
                } else if (wordMeaning.hasTags(Word.TAG_WEI)) {
                    word.setStrangeDegree(Word.DEGREE_WEI);
                }
            }
        }
        updateWord(word, false);
    }


    public Word oldWord2NewWord(OldWord oldWord) {
        Word word = new Word();
        word.setName(oldWord.getName());

        // 放入词义
        word.setInputMeaning(oldWord.getInputMeaning()); // 放入输入的

        List<Word.WordMeaning> newMeaningList = word.getMeaningList();
        for (OldWord.WordMeaning oldMeaning : oldWord.getMeaningList()) {
            Word.WordMeaning newMeaning = new Word.WordMeaning(); // 创建

            newMeaning.setCiXing(oldMeaning.getCiXing()); // 设置
            newMeaning.setMeaning(oldMeaning.getMeaning());
            newMeaning.setTagList(oldMeaning.getTagList());

            newMeaningList.add(newMeaning); // 加入
        }
        word.setMeaningList(newMeaningList); // 加入word

        // 放入相似的词
        word.setInputMeaning(oldWord.getInputMeaning()); // 放入输入的

        List<Word.SimilarWord> newSimilarList = new ArrayList<>();
        for (String oldSimilar : oldWord.getSimilarWordList()) {
            Word.SimilarWord newSimilar = new Word.SimilarWord(); //创建

            newSimilar.setName(oldSimilar); // 设置

            newSimilarList.add(newSimilar); // 加入
        }
        word.setSimilarWordList(newSimilarList); // 加入word

        // 放入陌生度以及上次记忆时间
        word.setStrangeDegree(oldWord.getStrangeDegree());
        word.setLastRememberTime(oldWord.getLastRememberTime());

        // 刷新数据库
        updateWord(word);
        return word;
    }

    public static GlobalData getInstance() {
        return H.instance;
    }

    public List<Word> getAllWord() {
        if (mAllWords == null) {
            queryAllWord();
        }
        return mAllWords;
    }

    public List<Word> getCurWords() {
        if (mCurWords == null) {
            queryAllWord();
        }
        return mCurWords;
    }

    public void setCurWords(List<Word> curWords) {
        mCurWords = curWords;
    }

    public void addWord(final Word word) {
        try {
            NetWorkUtil.saveWordService(new BmobWord(word), new NetWorkUtil.UploadListener() {
                @Override
                public void uploadSuccess() {
                    word.setLastUploadTime(System.currentTimeMillis());
                }

                @Override
                public void uploadFailed(BmobException e) {

                }
            });
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

    /**
     * 可以控制是否上传到网络，{@link #updateWord(Word, boolean)}
     * @param word
     */
    public void updateWord(Word word) {
        updateWord(word, true);
    }

    public void updateWord(final Word word, boolean isUpload) {
        try {
            if (isUpload) {
                NetWorkUtil.upLoadWord(word, new NetWorkUtil.UploadListener() {
                    @Override
                    public void uploadSuccess() {
                        word.setLastUploadTime(System.currentTimeMillis());
                    }

                    @Override
                    public void uploadFailed(BmobException e) {

                    }
                });
            }
            MyDatabase.getInstance().insertWord(word.getName(), new Gson().toJson(word));
            Logcat.e(word.getName() + "已更新");
            // 更新，内存中的已经更新了，不用在更新
        } catch (IOException e) {
            e.printStackTrace();
            Logcat.d(word.getName() + "更新失败 \n" + e.getMessage());
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public void deleteWord(Word word) {
        try {
            MyDatabase.getInstance().deleteWord(word.getName());
            NetWorkUtil.deleteWord(word, null);
            mAllWords.remove(word);
            mCurWords.remove(word);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public List<String> getRecentCmd() {
        if (recentCmdList == null) {
            readRecentCmd();
        }
        return recentCmdList;
    }

    public void updateInputCmd(String cmd) {
        if (cmd.trim().isEmpty()) return;
        int id = recentCmdList.indexOf(cmd); // 先检查是否存在

        if (id < 0) {
            if (recentCmdList.size() > AppConstant.RECENT_CMD_NUMBER) {
                recentCmdList.remove(recentCmdList.size() - 1);
            }
            recentCmdList.add(0, cmd);
        } else { // 放到最开始位置
            recentCmdList.add(0, recentCmdList.remove(id));
        }
    }

    public void saveRecentCmd() {
        SpUtil.saveRecentCmd(recentCmdList);
    }

    public void readRecentCmd() {
        recentCmdList = SpUtil.getRecentCmdList();
        int start = recentCmdList.size() / 3;
        for (String oneCmd : Command.INPUT_COMMAND_LIST) {
            if (!recentCmdList.contains(oneCmd)) {
                recentCmdList.add(start++, oneCmd);
            }
        }
    }

    public boolean isCheckedUser() {
        return mIsCheckedUser;
    }

    public void setCheckedUser(boolean isChecked) {
        mIsCheckedUser = isChecked;
    }

    public User getUser() {
        return mUser;
    }

    public boolean saveUserDate() {
        boolean res;
        res = SpUtil.saveUserName(mUser.getName());
        res &= SpUtil.saveUserPassword(mUser.getPassword());
        return res;
    }
}
