package com.lgc.memorynote.data;


import com.lgc.memorynote.wordList.Command;

public class AppConstant {
    /**
     * 单词输入格式错误
     */
    public static final int WORD_FORMAT_ERROR = 1;

    /**
     * 单词重复
     */
    public static final int REPETITIVE_WORD   = 2;

    /**
     * 没有输入有效数据
     */
    public static final int WORD_IS_NULL      =3;

    /**
     * 最近命令的数量
     */
    public static final int RECENT_CMD_LIMIT = Command.INPUT_COMMAND_HINT_LIST.size() + 6;

    /**
     * 最近命令的分隔符
     */
    public static final String RECENT_CMD_DIVIDER = "%^&%^&^$&";
}
