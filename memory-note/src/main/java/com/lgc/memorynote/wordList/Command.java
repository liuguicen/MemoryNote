package com.lgc.memorynote.wordList;

import com.lgc.memorynote.data.SearchUtil;
import com.lgc.memorynote.data.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小命令行系统中的命令
 * 目前的命令分两种，一种是搜索的，可以是单词，短语，汉语意思
 * 另一种是规定好的命令，以--开头，
 */
public class Command {
    /************************* 排序相关的命令***************************/
    public static final String COMMAND_START = "--";
    /** 按不熟悉度 */
    public static final String _stra  = COMMAND_START + "stra";
    public static final String _stra_ui  = "不熟";
    public static final int _stra_id  = 1;
    /** 按最后记忆时间 */
    public static final String _last  = COMMAND_START + "last";
    public static final String _last_ui  = "时间";
    public static final int _last_id  = 2;
    /** 按字典序 */
    public static final String _dict  = COMMAND_START + "dict";
    public static final String _dict_ui  = "字母";
    public static final int _dict_id  = 3;
    /** 按长度 */
    public static final String _len   = COMMAND_START + "len";
    public static final String _len_ui   = "长度";
    public static final int _len_id   = 4;
    /** 按相似的词的数量 */
    public static final String _sn = COMMAND_START + "simn";
    public static final String _sn_ui = "相数";
    public static final int _sn_id    = 5;


    /** 反向排序 */
    public static final String _rev   = COMMAND_START + "rev";
    public static final String _rev_ui   = "反向";
    public static final int _rev_id   = 6;


    /*********************** 过滤相关 *******************************/
    /** 只显示单词 */
    public static final String _word   = COMMAND_START + "name";
    public static final String _word_ui   = "单词";
    public static final int _word_id   = 7;
    /** 只显示短语 */
    public static final String _phr    = COMMAND_START + "phr";
    public static final String _phr_ui    = "短语";
    public static final int _phr_id    = 8;
    /** 只显示生的单词 */
    public static final String _sheng  =  Word.WordMeaning.TAG_SHENG;
    public static final String _sheng_ui  =  "生的";
    public static final int _sheng_id  = 9;
    /** 只显示词义怪的词 */
    public static final String _guai   =  Word.WordMeaning.TAG_GUAI;
    public static final String _guai_ui   =  "怪的";
    public static final int _guai_id   = 10;
    /** 把近似的词放到一起 */
    public static final String _sim    = COMMAND_START + "sim";
    public static final String _sim_ui    =  "相似";
    public static final int _sim_id    = 11;

    /**
     * UI 功能
     */
    /** 隐藏词义 **/
    public static final String _hdm = COMMAND_START + "hdm";
    public static final String _hdm_ui = "藏义";
    public static final int _hdm_id    = 12;

    /** 隐藏单词 **/
    public static final String _hdw = COMMAND_START + "hdw";
    public static final String _hdw_ui = "藏词";
    public static final int _hdw_id    = 13;


    public static final List<String> commandList = new ArrayList<String>(){{
            add(_stra);  add(_last);

            add(_hdm);   add(_hdw);   add(_sheng); add(_guai);

            add(_dict);  add(_len);   add(_sn);    add(_rev);

            add(_word);  add(_phr);   add(_sim);}};

    public static final Map<String, String> UICommandMap = new HashMap<String, String>(){{
            put(_stra, _stra_ui);  put(_last, _last_ui);

            put(_hdw,  _hdw_ui);   put(_hdm, _hdm_ui);    put(_sheng, _sheng_ui);  put(_guai, _guai_ui);

            put(_dict, _dict_ui);  put(_len , _len_ui);   put(_sn, _sn_ui);        put(_rev, _rev_ui);

            put(_word, _word_ui);  put(_phr,  _phr_ui);   put(_sim, _sim_ui);
    }};

    /*******************输入的命令******************/
    /** 进入设置 **/
    public static final String OPEN_SETTING     = COMMAND_START + "setting";
    public static final int OPEN_SETTING_ID     = 14;

    /** 显示单词数量 **/
    public static final String WORD_NUMBER      = COMMAND_START + "number";
    public static final int WORD_NUMBER_ID      = 15;

    /** 使用正则式搜索 **/
    public static final String REGEX_SERACH     = COMMAND_START + "re";
    public static final int REGEX_SEARCH_ID     = 16;

    /** 记录当前记忆的位置 **/
    public static final String RMB              = COMMAND_START + "rmb";
    public static final int RMB_ID              = 17;

    /** 恢复到上次记忆的位置 **/
    public static final String RST              = COMMAND_START + "rst";
    public static final int RST_ID              = 18;

    /** 陌生度 **/
    public static final String STRANGE_DEGREE   = COMMAND_START + "sd";
    public static final int STRANGE_DEGREE_ID   = 19;

    /** 搜索所有数据 */
    public static final String GLOBAL = "-g";
    public static final int GLOBAL_ID   = 20;


    public static final List<String> INPUT_COMMAND_LIST = new ArrayList<String>(){{
        add(REGEX_SERACH);  add(RMB);           add(RST);
        add(OPEN_SETTING);  add(WORD_NUMBER);   add(STRANGE_DEGREE);
    }};

    public static final String TAG_START = Word.TAG_START;
    public static final String commandGuide
                   = "   " + OPEN_SETTING + " 进入设置界面\n"
                   + "   " + WORD_NUMBER + " 当前列表单词数量\n"
                   + "   " + REGEX_SERACH + " 正则式搜索，仅支持单词名称\n"
                   + "   " + RMB + " 记录当前的记忆位置\n"
                   + "   " + RST + " 恢复上次记忆位置\n"
                   + "   " + STRANGE_DEGREE  + "陌生度过滤， 比如" + STRANGE_DEGREE +">8" + "\n"
                   + "   " + GLOBAL + "加载搜索命令后面，搜索单词所有数据"
                   + "   " + TAG_START + " 对单词添加标志，说明它的一些特性，比如词义怪，是个短语、词组等";

    /**
     * 排序，会生成一个新的列表，不改变原来的数据
     * @param commandList 必须是标准的命令才有效
     * @param search 用户输入的搜索命令
     */
    public static List<Word> orderByCommand(String search, List<String> commandList, List<Word> wordList) throws Exception {
        List<Word> resultList = new ArrayList<>(wordList.size());
        resultList.addAll(wordList);

        // 第一步，如果是搜索，搜索出满足条件的
        resultList = SearchUtil.searchMultiAspects(search, resultList);

        // 第二步，进行过滤操作
        for (int i = commandList.size() - 1; i >= 0; i--) {
            String grep = commandList.get(i);
            if (_phr.equals(grep)) {
                SearchUtil.grepNotPhrase(resultList);
            } else if (_word.equals(grep)) {
                SearchUtil.grepNotWord(resultList);
            } else if (grep != null && grep.startsWith(Word.TAG_START)) {
                SearchUtil.grepNoTag(grep, resultList);
            }

        }

        // 第三步，进行排序操作
        if (commandList.size() > 0) {
            Collections.sort(resultList, SortUtil.getComparator(commandList));
        }
        return resultList;
    }
}