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
    private static List<Word> mShowWords = new ArrayList<>();

    /**
     * 最近输入的命令，存下来
     */
    private static List<String> recentInputCmdList;

    /**
     * 最近的命令，只放到内存中
     */
    private static List<SearchData> mRecentSearch;
    public static SearchData mCurSearch;
    public static final int MAX_RECENT_CMD_SIZE = 5;

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
        mRecentSearch = new ArrayList<>(5);
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
                    mShowWords.add(word);
                }


                /** 千万小心 ！！
                 *
                 preProcess(word);
                 convertWordFromat(word);
                 * word数据结构变化的时候用， 把原版的Word拷一份出来了，命名为OldWord，去掉上面几行代码，
                 * 使用下面的代码，在oldWord2NewWord方法里面加上相关逻辑
                 *
                 *
                 * OldWord oldWord = gson.fromJson(oneJsonWord, OldWord.class);
                 * oldWord2NewWord(oldWord);
                 * 看效果加上这两行，不关掉应用可能会改到数据库
                 * mAllWords.add(word);
                 * mShowWords.add(word);
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

    /** 千万小心 ！！
     */
    private void preProcess(final Word word) {
        /*if (word.hasTag(Word.TAG_DI)) {
            word.setStrangeDegree(Word.DEGREE_DI);
        } else if (word.hasTag(Word.TAG_ROOT)) {
            word.setStrangeDegree(Word.DEGREE_ROOT);
        } else if (word.hasTag(Word.TAG_PREFFIX)) {
            word.setStrangeDegree(Word.DEGREE_PREFFIX);
        } else if (word.hasTags(WordUtil.SUFFIX_LIST)) {
            word.setStrangeDegree(Word.DEGREE_SUFFIX);
        } else if (word.hasTag(Word.TAG_WEI)) {
            word.setStrangeDegree(Word.DEGREE_WEI);
        }*/
      /*  String im = word.getInputMeaning();
        if (im.contains("(查)")||im.contains("(已查") || im.contains("（查）")||im.contains("（已查）")) {
            word.setCheckedMeaning(true);
        }
        updateWord(word, false);*/
    }


    public Word oldWord2NewWord(OldWord oldWord) {
        Word word = new Word();
        word.setName(oldWord.getName());

        // 放入词义
        word.setInputMeaning(oldWord.getInputMeaning()); // 放入输入的

        word.setTagList(oldWord.getTagList());
        List<Word.WordMeaning> newMeaningList = word.getMeaningList();
        for (OldWord.WordMeaning oldMeaning : oldWord.getMeaningList()) {
            Word.WordMeaning newMeaning = new Word.WordMeaning(); // 创建

            newMeaning.setCiXing(oldMeaning.getCiXing()); // 设置
            newMeaning.setMeaning(oldMeaning.getMeaning());

            newMeaningList.add(newMeaning); // 加入
        }
        word.setMeaningList(newMeaningList); // 加入word

        // 放入相似的词
        word.setInputMeaning(oldWord.getInputMeaning()); // 放入输入的

        List<Word.SimilarWord> newSimilarList = new ArrayList<>();
        word.setSimilarWordList(newSimilarList); // 加入word

        // 放入陌生度以及上次记忆时间
        word.setStrangeDegree(oldWord.getStrangeDegree());
        word.setLastRememberTime(oldWord.getLastRememberTime());

        // 刷新数据库
        updateWord(word);
        return word;
    }


    public void refreshByRemoteData(List<BmobWord> resultList) {
        Gson gson = new Gson();
        for (BmobWord bmobWord : resultList) {
            Word remoteWord = gson.fromJson(bmobWord.getJsonData(), Word.class);
            int localId = mAllWords.indexOf(remoteWord);

            if (localId < 0 || (localId >= 0 && remoteWord.getLastModifyTime()
                    > mAllWords.get(localId).getLastModifyTime())) { // 远程的更新时间更长才更新
                if (localId < 0) {
                    mAllWords.add(remoteWord);
                } else {
                    mAllWords.set(localId, remoteWord);
                }
                updateWord(remoteWord, false);
            }
        }
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

    public List<Word> getShowWords() {
        if (mShowWords == null) {
            queryAllWord();
        }
        return mShowWords;
    }

    public void setCurWords(List<Word> curWords) {
        mShowWords = curWords;
    }

    public void addWord(final Word word) {
        String jsonData = new Gson().toJson(word);
        try {
            NetWorkUtil.saveWordService(new BmobWord(word, jsonData), new NetWorkUtil.UploadListener() {
                @Override
                public void uploadSuccess() {
                    word.setLastUploadTime(System.currentTimeMillis());
                }

                @Override
                public void uploadFailed(BmobException e) {

                }
            });
            MyDatabase.getInstance().insertWord(word.getName(), jsonData);
            mAllWords.add(word);
            mShowWords.add(word);
        } catch (IOException e) {
            e.printStackTrace();
            Logcat.d(e.getMessage());
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    /**
     * 可以控制是否上传到网络，{@link #updateWord(Word, boolean)}
     *
     * @param word
     */
    public void updateWord(Word word) {
        updateWord(word, true);
    }

    public void updateWord(final Word word, boolean isUpload) {
        String jsonData =  new Gson().toJson(word);
        try {
            if (isUpload) {
                NetWorkUtil.upLoadWord(word, jsonData, new NetWorkUtil.UploadListener() {
                    @Override
                    public void uploadSuccess() {
                        word.setLastUploadTime(System.currentTimeMillis());
                    }

                    @Override
                    public void uploadFailed(BmobException e) {

                    }
                });
            }
            MyDatabase.getInstance().insertWord(word.getName(), jsonData);
            Logcat.e(word.getName() + "数据库已更新");
            // 更新，内存中的已经更新了，不用在更新
        } catch (IOException e) {
            e.printStackTrace();
            Logcat.d(word.getName() + "数据库更新失败 \n" + e.getMessage());
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public void deleteWord(Word word) {
        try {
            MyDatabase.getInstance().deleteWord(word.getName());
            NetWorkUtil.deleteWord(word, null);
            mAllWords.remove(word);
            mShowWords.remove(word);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MyDatabase.getInstance().close();
        }
    }

    public List<String> getRecentCmd() {
        if (recentInputCmdList == null) {
            readRecentInputCmd();
        }
        return recentInputCmdList;
    }

    public void updateInputCmd(String cmd) {
        if (cmd.trim().isEmpty()) return;
        int id = recentInputCmdList.indexOf(cmd); // 先检查是否存在
        // Command.INPUT_COMMAND_HINT_LIST 在最前面
        if (id < 0) {
            if (recentInputCmdList.size() > AppConstant.RECENT_CMD_LIMIT) {
                removeLastInputCmd(1);
            }
        } else { // 放到最开始位置
           recentInputCmdList.remove(id);
        }
        recentInputCmdList.add(0, cmd);
    }

    private void removeLastInputCmd(int number) {
        for (int i = recentInputCmdList.size() - 1; i >= 0 ; i--) {
            if (!Command.INPUT_COMMAND_HINT_LIST.contains(recentInputCmdList.get(i))) {
                if (number-- > 0) {
                    recentInputCmdList.remove(i);
                } else {
                    break;
                }
            }
        }
    }

    public static final int FRONT_CMD_NUMBER = 3;

    public void saveRecentInputCmd() {
        if (recentInputCmdList.size() > AppConstant.RECENT_CMD_LIMIT) {
            removeLastInputCmd(recentInputCmdList.size() - AppConstant.RECENT_CMD_LIMIT);
        }
        SpUtil.saveRecentCmd(recentInputCmdList);
    }

    public void readRecentInputCmd() {
        recentInputCmdList = SpUtil.getRecentCmdList();
        if (recentInputCmdList.size() > AppConstant.RECENT_CMD_LIMIT) {
            removeLastInputCmd(recentInputCmdList.size() - AppConstant.RECENT_CMD_LIMIT);
        }
        // Command.INPUT_COMMAND_HINT_LIST 在最前面
        for (String oneCmd : Command.INPUT_COMMAND_HINT_LIST) {
            if (!recentInputCmdList.contains(oneCmd)) {
                recentInputCmdList.add(0, oneCmd);
            }
        }
    }

    public void addLastSearchData(SearchData searchDate) {
        mRecentSearch.add(searchDate);
        if (mRecentSearch.size() > MAX_RECENT_CMD_SIZE) {
            mRecentSearch.remove(0);
        }
        mCurSearch = null;
    }

    public SearchData getPrevSearch() {
        if (mCurSearch == null) {
            if (mRecentSearch.size() > 0) {
                mCurSearch = mRecentSearch.get(mRecentSearch.size() - 1);
            }
        } else {
            int id = mRecentSearch.indexOf(mCurSearch);
            if (id >= 1 && id < mRecentSearch.size()) {
                mCurSearch = mRecentSearch.get(id - 1);
            } else if (mRecentSearch.size() > 0) {
                mCurSearch = mRecentSearch.get(0);
            }
        }
        return mCurSearch;
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
