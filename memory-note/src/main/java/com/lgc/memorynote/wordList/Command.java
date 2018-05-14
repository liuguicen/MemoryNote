package com.lgc.memorynote.wordList;

import com.lgc.memorynote.data.SearchUtil;
import com.lgc.memorynote.data.Word;
import com.lgc.memorynote.data.WordWithComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 小命令行系统中的命令，模仿成熟的命令行系统，
 * 这里多了通过点击添加命令的功能，一般的命令行就是输入命令，点击的时候，点击一个就相当于输入了该命令
 */
public class Command {
    /************************* 排序相关的命令***************************/
    public static final String COMMAND_START = "--";
    /** 按不熟悉度 */
    public static final String _stra  = COMMAND_START + "stra";
    public static final String _stra_ui  = "不熟";
    public static final int _stra_id  = 1;
    /** 按最后记忆时间 */
    public static final String _time = COMMAND_START + "time";
    public static final String _time_ui = "时间";
    public static final int _time_id = 2;
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
    public static final String _sheng  =  Word.TAG_SHENG;
    public static final String _sheng_ui  =  "生的";
    public static final int _sheng_id  = 9;
    /** 只显示词义怪的词 */
    public static final String _guai   =  Word.TAG_GUAI;
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

    /**  上一次的命令 **/
    public static final String _lc = COMMAND_START + "lc";
    public static final String _lc_ui = "上次";
    public static final int _lc_id = 14;


    /*******************输入的命令******************/
    // 这里想法有些偏差，实际上输入的命令和点击的命令应该是同种类型的，只不过多了UI，不用特别分开，统一的才能一起处理，相互转化，反之则麻烦
    /** 进入设置 **/
    public static final String _open_setting = COMMAND_START + "setting";
    public static final int OPEN_SETTING_ID     = 14;

    /** 显示单词数量 **/
    public static final String _word_number = COMMAND_START + "number";
    public static final int WORD_NUMBER_ID      = 15;

    /** 使用正则式搜索 **/
    public static final String _regex_search = COMMAND_START + "re";
    public static final int REGEX_SEARCH_ID     = 16;

    /** 记录当前记忆的位置 **/
    public static final String _rmb = COMMAND_START + "rmb";
    public static final String _rmb_ui = "记录";
    public static final int RMB_ID              = 17;

    /** 恢复到上次记忆的位置 **/
    public static final String _restore = COMMAND_START + "rst";
    public static final int RST_ID              = 18;

    /** 陌生度 **/
    public static final String _strange_degree = COMMAND_START + "sd";
    public static final int STRANGE_DEGREE_ID   = 19;

    /** 搜索所有数据 */
    public static final String _global = "-g";
    public static final int GLOBAL_ID   = 20;

    /** 距今时间 **/
    public static final String _time_to_now = COMMAND_START + "ttn";

    // 加入顺序就是显示顺序
    public static final Map<String, String> UI_COMMAND_MAP = new LinkedHashMap<String, String>(){{
        put(_stra, _stra_ui);    put(_time, _time_ui);   put(_hdm, _hdm_ui);     put(_dict, _dict_ui);

        put(_sheng, _sheng_ui);  put(_guai, _guai_ui);   put(_len , _len_ui);    put(_lc, _lc_ui);

        put(_sn, _sn_ui);        put(_rev, _rev_ui);     put(_rmb,  _rmb_ui);    put(_word, _word_ui);

        put(_phr,  _phr_ui);     put(_sim, _sim_ui);
    }};

    // 要加到可点击列表中的
    public static final List<String> CLICKABLE_CMD_LIST = new ArrayList<>();
    static {
        for (Map.Entry<String, String> entry : UI_COMMAND_MAP.entrySet()) {
                CLICKABLE_CMD_LIST.add(entry.getKey());
        }
    }

    public static final List<String> INPUT_COMMAND_HINT_LIST = new ArrayList<String>(){{
        add(_regex_search);  add(_hdw);           add(_restore);
        add(_open_setting);  add(_word_number);   add(_strange_degree);
        add(_time_to_now);
    }};

    public static final String TAG_START = Word.TAG_START;
    public static final String commandGuide
                   = "   " + _open_setting + " 进入设置界面\n"
                   + "   " + _word_number + " 当前列表单词数量\n"
                   + "   " + _regex_search + " 正则式搜索，仅支持单词名称\n"
                   + "   " + _hdw + " 隐藏单词\n"
                   + "   " + _restore + " 恢复上次记忆位置\n"
                   + "   " + _strange_degree + "陌生度过滤， 比如" + _strange_degree +">8" + "\n"
                   + "   " + _global + "加载搜索命令后面，搜索单词所有数据\n"
                   + "   " + _sn_ui + "相似单词的数量\n"
                   + "   " + TAG_START + " 对单词添加标志，说明它的一些特性，比如词义怪，是个短语、词组等";


    /**
     * 主要包括排序和过滤，会生成一个新的列表，不改变原来的数据
     * @param commandList 必须是标准的命令才有效, 注意用过不会再用的命令，会从命令列表里面移除
     * @param search 用户输入的搜索命令
     */
    public static List<Word> orderByCommand(String search, List<String> commandList, List<Word> wordList) throws NumberFormatException{
        List<WordWithComparator> resultList = new ArrayList<>(wordList.size());

        // 第一步，如果是搜索，搜索出满足条件的, 并加上排序数据
        boolean needSort = SearchUtil.searchMultiAspects(search, wordList, resultList);

        // 第二步，进行过滤操作
        for (int i = commandList.size() - 1; i >= 0; i--) {
            String grep = commandList.get(i);
            if (_phr.equals(grep)) {
                SearchUtil.grepNotPhrase(resultList);
                commandList.remove(i);
            } else if (_word.equals(grep)) {
                SearchUtil.grepNotWord(resultList);
                commandList.remove(i);
            } else if (grep != null && grep.startsWith(Word.TAG_START)) {
                SearchUtil.grepNoTag(grep, resultList);
                commandList.remove(i);
            }
        }

        // 第三步，进行排序操作

        if (needSort) {
            Comparator<WordWithComparator> comparator = new SortUtil.WordComparator(commandList); // 注意用过不会再用的命令，会从命令列表里面移除
            int id = commandList.indexOf(Command._rev);
            if (id >= 0) {
                comparator = Collections.reverseOrder(comparator);
                commandList.remove(id);
            }
            Collections.sort(resultList, comparator);
        }

        // 第四步，返回结果
        List<Word> returnList = new ArrayList<>();
        for (WordWithComparator wordWithComparator : resultList) {
            returnList.add(wordWithComparator.getWord());
        }
        return returnList;
    }
}