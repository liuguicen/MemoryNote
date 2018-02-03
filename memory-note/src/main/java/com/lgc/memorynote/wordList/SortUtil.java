package com.lgc.memorynote.wordList;

import com.lgc.memorynote.wordDetail.Word;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <pre>
 *      author : liuguicen
 *      time : 2018/02/01
 *      version : 1.0
 * <pre>
 */

public class SortUtil {

    public static final String DEFAULT_SORT_COMMAND = Command._stra;

    public static String getHingString() {
        return "请输入命令";
    }

    @Deprecated
    public static List<String> parseOrderCommand(String command) {
        if (command == null) return null;
        command = command.trim();
        if (command.isEmpty()) return null;
        List<String> orderList = new ArrayList<>();
        String[] perhapsOrder = command.split("|");
        for (String order : perhapsOrder) {
            order = order.trim();
            orderList.add(order);
        }
        return orderList;
    }

    public static Comparator<Word> getComparator(List<String> commandList) {
        if (commandList == null) {
            commandList = new ArrayList<>();
        }
        if (commandList.isEmpty()) {
            commandList.add(DEFAULT_SORT_COMMAND);
        }
        return new WordComparator(commandList);
    }

    public static class WordComparator implements Comparator<Word> {
        final List<String> finalOrderList;

        public WordComparator(List<String> finalOrderList) {
            this.finalOrderList = finalOrderList;
        }

        @Override
        public int compare(Word o1, Word o2) {
            if (o1 == null && o2 == null) return 0;
            if (o1 == null) return 1;
            if (o2 == null) return -1;

                /*从前面往后一直遍历命令，先遍历到的就以这个命令的方式比较，
                 * 如果这种方式比较结果相等，那么继续遍历下一种方式
                 */
            int re = 0;
            for(String order : finalOrderList) {
                if(Command._stra.equals(order)) {
                    re = Word.compareStrangeDegree(o1.getStrangeDegree(), o2.getStrangeDegree());
                }  else if (Command._last.equals(order)) {
                    re = Word.compareRememberTime(o1.getLastRememberTime(), o2.getLastRememberTime());
                } else if (Command._sn.equals(order)) {
                    re = Word.compareSimilarNumber(o1.getSimilarWordList(), o2.getSimilarWordList());
                } else if (Command._dict.equals(order)) {
                    re = Word.compareDictionary(o1.getName(), o2.getName());
                } else if (Command._len.contains(order)) {
                    re = Word.compareLength(o1.getName(), o2.getName());
                }
                if (re != 0) return re;
            }
            return re;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };
}
