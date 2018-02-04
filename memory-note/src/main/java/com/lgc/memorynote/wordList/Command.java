package com.lgc.memorynote.wordList;

import android.widget.TextView;

import com.lgc.memorynote.data.SearchUtil;
import com.lgc.memorynote.wordDetail.Word;

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
    /** 按最后记忆时间 */
    public static final String _last  = COMMAND_START + "last";
    public static final String _last_ui  = "时间";
    /** 按字典序 */
    public static final String _dict  = COMMAND_START + "dict";
    public static final String _dict_ui  = "字母";
    /** 按长度 */
    public static final String _len   = COMMAND_START + "len";
    public static final String _len_ui   = "长度";
    /** 按相似的词的数量 */
    public static final String _sn = COMMAND_START + "simn";
    public static final String _sn_ui = "相数";


    /** 反向排序 */
    public static final String _rev   = COMMAND_START + "rev";
    public static final String _rev_ui   = "反向";


    /*********************** 过滤相关 *******************************/
    /** 只显示单词 */
    public static final String _word   = COMMAND_START + "name";
    public static final String _word_ui   = "单词";
    /** 只显示短语 */
    public static final String _phr    = COMMAND_START + "phr";
    public static final String _phr_ui    = "短语";
    /** 只显示生的单词 */
    public static final String _sheng  =  Word.WordMeaning.TAG_SHENG;
    public static final String _sheng_ui  =  "生的";
    /** 只显示词义怪的词 */
    public static final String _guai   =  Word.WordMeaning.TAG_GUAI;
    public static final String _guai_ui   =  "怪的";
    /** 把近似的词放到一起 */
    public static final String _sim    = COMMAND_START + "sim";
    public static final String _sim_ui    =  "相似";

    public static final List<String> commandList = new ArrayList<String>(){{
        add(_stra); add(_last); add(_dict); add(_len); add(_sn); add(_rev); add(_word); add(_phr); add(_sheng); add(_guai); add(_sim);}};
    public static final Map<String, String> UICommandMap = new HashMap<String, String>(){{
            put(_stra, _stra_ui);  put(_last, _last_ui);  put(_dict, _dict_ui);  put(_len ,_len_ui);
            put(_sn, _sn_ui);
            put(_rev, _rev_ui);    put(_word, _word_ui);  put(_phr, _phr_ui);    put(_sheng, _sheng_ui);
            put(_guai, _guai_ui);  put(_sim, _sim_ui);

    }};

    /**
     * 排序，会生成一个新的列表，不改变原来的数据
     * @param commandList 必须是标准的命令才有效
     * @param search 用户输入的搜索命令
     */
    public static List<Word> orderByCommand(String search, List<String> commandList, List<Word> wordList) {
        List<Word> resultList = new ArrayList<>(wordList.size());
        resultList.addAll(wordList);

        // 第一步，如果是搜索，搜索出满足条件的
        SearchUtil.searchWordOrMeaning(search, resultList);

        // 第二步，进行过滤操作
        for (int i = commandList.size() - 1; i >= 0; i--) {
            String grep = commandList.get(i);
            if (_phr.equals(grep)) {
                SearchUtil.grepNotPhrase(resultList);
            } else if (_word.equals(grep)) {
                SearchUtil.grepNotWord(resultList);
            } else if (grep != null && grep.startsWith(Word.WordMeaning.TAG_START)) {
                SearchUtil.grepNoTag(grep, resultList);
            }

        }

        // 第三步，进行排序操作
        Collections.sort(resultList, SortUtil.getComparator(commandList));
        return resultList;
    }
}
